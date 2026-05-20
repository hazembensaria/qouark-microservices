package com.infotexa.storageservice.service.implimentation;

import com.infotexa.storageservice.domain.FileDownloadResult;
import com.infotexa.storageservice.dtoRequest.ShareRequest;
import com.infotexa.storageservice.exception.ApiException;
import com.infotexa.storageservice.model.StorageFile;
import com.infotexa.storageservice.model.StorageFolder;
import com.infotexa.storageservice.repository.StorageRepository;
import com.infotexa.storageservice.service.StorageService;
import com.infotexa.storageservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.infotexa.storageservice.consatant.Constant.PHOTO_DIRECTORY;
import static com.infotexa.storageservice.consatant.Constant.STORAGE_DIRECTORY;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.springframework.util.StringUtils.cleanPath;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final StorageRepository storageRepository;
    private final UserService userService;

    // change later to real storage (S3 / disk)
    private final String BASE_STORAGE_PATH = "/storage/";

    // ================= FOLDERS =================

    @Override
    public StorageFolder createFolder(String userUuid, String parentFolderUuid, String name) {

        if (name == null || name.isBlank()) {
            throw new ApiException("Folder name cannot be empty");
        }
        String folderUuid = UUID.randomUUID().toString();
        return storageRepository.createFolder(userUuid, parentFolderUuid, folderUuid, name);
    }

    @Override
    public List<StorageFolder> getFolders(String folderUuid) {
        return storageRepository.getFolders(folderUuid);
    }

    @Override
    public StorageFolder getFolder(String folderUuid) {
        return storageRepository.getFolderByUuid(folderUuid);
    }

    // ================= FILE UPLOAD =================

//    @Override
//    public StorageFile uploadFile(String userUuid, String folderUuid, MultipartFile file) {
//
//        try {
//            var user = userService.getUserByUuid(userUuid);
//
//            if (file == null || file.isEmpty()) {
//                throw new ApiException("File is empty");
//            }
//
//            String fileUuid = UUID.randomUUID().toString();
//
//            String originalName = file.getOriginalFilename();
//            String extension = extractExtension(originalName);
//
//            long size = file.getSize();
//            String formattedSize = formatSize(size);
//
//            String storagePath = BASE_STORAGE_PATH + userUuid + "/" + folderUuid + "/" + fileUuid;
//
//            String uri = "/files/" + fileUuid;
//
//            // 1. save file physically (LOCAL STORAGE FOR NOW)
//            saveToDisk(storagePath, file);
//
//            // 2. save in DB
//            return storageRepository.saveFile(
//                    fileUuid,
//                    userUuid,
//                    folderUuid,
//                    originalName,
//                    extension,
//                    size,
//                    formattedSize,
//                    storagePath,
//                    uri
//            );
//
//        } catch (Exception e) {
//            log.error("uploadFile error: {}", e.getMessage());
//            throw new ApiException("Error uploading file");
//        }
//    }

    public List<StorageFile> uploadFile(String userUuid, String folderUuid, List<MultipartFile> files) {
        try {
            var uploadedFiles = new ArrayList<StorageFile>();

            if (files == null || files.isEmpty()) {
                return uploadedFiles;
            }

            for (MultipartFile file : files) {

                if (file.isEmpty()) continue;

                String originalName = Objects.requireNonNull(
                        file.getOriginalFilename(),
                        "File name is missing"
                );

                if (originalName.isBlank()) {
                    continue;
                }

                String fileUuid = UUID.randomUUID().toString();

                String cleanName = cleanPath(originalName);
                String extension = getExtension(originalName);

                long size = file.getSize();
                String formattedSize = byteCountToDisplaySize(size);

                String storagePath = STORAGE_DIRECTORY + userUuid + "/" + folderUuid;

                Files.createDirectories(Paths.get(storagePath));

                Path fileStorage = Paths.get(storagePath)
                        .resolve(fileUuid + "_" + cleanName)
                        .normalize();

                Files.copy(
                        file.getInputStream(),
                        fileStorage,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );

                String uri = "/storage/files/" + fileUuid;

                var savedFile = storageRepository.saveFile(
                        fileUuid,
                        userUuid,
                        folderUuid,
                        cleanName,
                        extension,
                        size,
                        formattedSize,
                        fileStorage.toString(),
                        uri
                );

                uploadedFiles.add(savedFile);
            }

            return uploadedFiles;

        } catch (Exception exception) {
            log.error("uploadFiles failed: userUuid={}, folderUuid={}", userUuid, folderUuid, exception);
            throw new ApiException("An error occurred while uploading files.");
        }
    }


    @Override
    public List<StorageFile> getFiles(String folderUuid) {
        return storageRepository.getFiles(folderUuid);
    }

    @Override
    public Boolean deleteFile(String fileUuid) {
        return storageRepository.deleteFile(fileUuid);
    }

    @Override
    public StorageFolder getRootFolder(String userUuid) {
        return storageRepository.getRootFolder(userUuid);
    }

    @Override
    public FileDownloadResult downloadFile(String userUuid, String fileUuid) {
        try {
            var attachment = storageRepository.getStorageFile(userUuid , fileUuid);
            Path filePath=Paths.get(attachment.getStoragePath()).toAbsolutePath().normalize();
            if(!Files.exists(filePath)){
                throw new ApiException("File not found on the server");
            }

            return new FileDownloadResult(filePath,attachment.getName());

        }catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");
        }
    }

    @Override
    public void shareFolder(String ownerUuid, ShareRequest request) {
        storageRepository.shareFolder(ownerUuid, request.getResourceUuid(), request.getSharedWithUserUuid(), request.getPermission());
    }

    @Override
    public void shareFile(String ownerUuid, ShareRequest request) {
        storageRepository.shareFile(ownerUuid, request.getResourceUuid(), request.getSharedWithUserUuid(), request.getPermission());
    }
}