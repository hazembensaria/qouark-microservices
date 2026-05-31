package com.infotexa.storageservice.repository.implimentation;

import com.infotexa.storageservice.exception.ApiException;
import com.infotexa.storageservice.model.StorageFile;
import com.infotexa.storageservice.model.StorageFolder;
import com.infotexa.storageservice.query.StorageQuery;
import com.infotexa.storageservice.repository.StorageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.infotexa.storageservice.query.StorageQuery.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageRepositoryImpl implements StorageRepository {

    private final JdbcClient jdbc;

    @Override
    public StorageFolder createFolder(String userUuid, String parentFolderUuid, String folderUuid, String name) {
        try {
            return jdbc.sql(CREATE_STORAGE_FOLDER)
                    .params(Map.of("userUuid", userUuid,"parentUuid", parentFolderUuid, "folderUuid", folderUuid, "name", name))
                    .query(StorageFolder.class)
                    .single();
        } catch (Exception e) {
            log.error("createFolder error: {}", e.getMessage());
            throw new ApiException("Error creating folder");
        }
    }

    @Override
    public List<StorageFolder> getFolders(String folderUuid) {
        try {
            return jdbc.sql(GET_STORAGE_FOLDERS)
                    .params(Map.of("folderUuid", folderUuid))
                    .query(StorageFolder.class)
                    .list();

        } catch (Exception e) {
            log.error("getFolders error: {}", e.getMessage());
            throw new ApiException("Error fetching folders");
        }
    }

    @Override
    public StorageFolder getFolderByUuid(String folderUuid) {
        try {
            return jdbc.sql(GET_FOLDER_BY_UUID)
                    .params(Map.of("folderUuid", folderUuid))
                    .query(StorageFolder.class)
                    .single();

        } catch (EmptyResultDataAccessException e) {
            throw new ApiException("Folder not found");
        }
    }

    @Override
    public StorageFile saveFile(
            String fileUuid,
            String userUuid,
            String folderUuid,
            String name,
            String extension,
            Long size,
            String formattedSize,
            String storagePath,
            String uri
    ) {
        try {
            return jdbc.sql(SAVE_STORAGE_FILE)
                    .params(Map.of(
                            "fileUuid", fileUuid,
                            "userUuid", userUuid,
                            "folderUuid", folderUuid,
                            "name", name,
                            "extension", extension,
                            "size", size,
                            "formattedSize", formattedSize,
                            "storagePath", storagePath,
                            "uri", uri
                    ))
                    .query(StorageFile.class)
                    .single();

        } catch (Exception e) {
            log.error("saveFile error: {}", e.getMessage());
            throw new ApiException("Error saving file");
        }
    }

    @Override
    public List<StorageFile> getFiles(String folderUuid) {
        try {
            return jdbc.sql(GET_STORAGE_FILES)
                    .params(Map.of("folderUuid", folderUuid))
                    .query(StorageFile.class)
                    .list();

        } catch (Exception e) {
            log.error("getFiles error: {}", e.getMessage());
            throw new ApiException("Error fetching files");
        }
    }
    @Override
    public void deleteFile(String fileUuid) {
        try {
             jdbc.sql(DELETE_File)
                    .params(Map.of("fileUuid", fileUuid))
                    .query(Boolean.class)
                    .single();

        } catch (Exception e) {
            log.error("deleteFile error: {}", e.getMessage());
            throw new ApiException("Error deleting file");
        }
    }
    @Override
    public String createPublicLink(String publicLinkUuid, String fileUuid, String token, String userUuid) {
        try {
            return jdbc.sql(CREATE_PUBLIC_LINK)
                    .params(Map.of(
                            "publicLinkUuid", publicLinkUuid,
                            "fileUuid", fileUuid,
                            "token", token,
                            "userUuid", userUuid
                    ))
                    .query(String.class)
                    .single();

        } catch (Exception e) {
            log.error("createPublicLink error: {}", e.getMessage());
            throw new ApiException("Error creating public link");
        }
    }

    @Override
    public boolean checkQuota(String userUuid, long fileSize) {
        try {
            return jdbc.sql(CHECK_QUOTA)
                    .params(Map.of(
                            "userUuid", userUuid,
                            "fileSize", fileSize
                    ))
                    .query(Boolean.class)
                    .single();

        } catch (Exception e) {
            log.error("checkQuota error: {}", e.getMessage());
            throw new ApiException("Quota check failed");
        }
    }

    @Override
    public StorageFolder getRootFolder(String userUuid) {
        try {
            return jdbc.sql(GET_ROOT_FOLDER)
                    .params(Map.of(
                            "userUuid", userUuid
                    ))
                    .query(StorageFolder.class)
                    .single();

        } catch (Exception e) {
            log.error("getRootFolder error: {}", e.getMessage());
            throw new ApiException("Error creating public link");
        }
    }

    public StorageFile getStorageFile(String userUuid, String fileUuid) {
        try {
            return jdbc.sql(SELECT_FILES_BY_USER_UUID_QUERY)
                    .params( Map.of("userUuid" , userUuid , "fileUuid" , fileUuid))
                    .query(StorageFile.class).single();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("file with userUuid %s not found", userUuid));
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");
        }
    }

    @Override
    public void shareFolder(
            String ownerUuid,
            String folderUuid,
            String sharedWithUserUuid,
            String permission
    ) {

        jdbc.sql(SHARE_FOLDER)
                .params(Map.of(
                        "ownerUuid", ownerUuid,
                        "folderUuid", folderUuid,
                        "sharedWithUserUuid", sharedWithUserUuid,
                        "permission", permission
                ))
                .query()
                .singleValue();
    }

    @Override
    public void shareFile(
            String ownerUuid,
            String fileUuid,
            String sharedWithUserUuid,
            String permission
    ) {

        jdbc.sql(SHARE_FILE)
                .params(Map.of(
                        "ownerUuid", ownerUuid,
                        "fileUuid", fileUuid,
                        "sharedWithUserUuid", sharedWithUserUuid,
                        "permission", permission
                ))
                .query()
                .singleValue();
    }

    @Override
    public List<StorageFolder> sharedFolders(String userUuid) {
        try {
            return jdbc.sql(SHARED_FOLDERS)
                    .params(Map.of("userUuid", userUuid))
                    .query(StorageFolder.class)
                    .list();

        } catch (Exception e) {
            log.error("sharedFolders error: {}", e.getMessage());
            throw new ApiException("Error fetching shared folders");
        }
    }

    @Override
    public List<StorageFile> sharedFiles(String userUuid) {
        try {
            return jdbc.sql(SHARED_FILES)
                    .params(Map.of("userUuid", userUuid))
                    .query(StorageFile.class)
                    .list();

        } catch (Exception e) {
            log.error("sharedFiles error: {}", e.getMessage());
            throw new ApiException("Error fetching shared files");
        }
    }

    @Override
    public void deleteFolder(String uuid) {
        try {
            jdbc.sql(DELETE_FOLDER)
                    .params(Map.of("folderUuid", uuid))
                    .query()
                    .singleValue();
        } catch (Exception e) {
            log.error("deleteFolder error: {}", e.getMessage());
            throw new ApiException("Error deleting folder");
        }
    }

    @Override
    public List<StorageFolder> getTrashFolders(String userUuid) {
        try {
            return jdbc.sql(GET_TRASH_FOLDERS)
                    .params(Map.of("userUuid", userUuid))
                    .query(StorageFolder.class)
                    .list();

        } catch (Exception e) {
            log.error("getTrashFolders error: {}", e.getMessage());
            throw new ApiException("Error fetching trash folders");
        }
    }

    @Override
    public List<StorageFile> getTrashFiles(String userUuid) {
        try {
            return jdbc.sql(GET_TRASH_FILES)
                    .params(Map.of("userUuid", userUuid))
                    .query(StorageFile.class)
                    .list();

        } catch (Exception e) {
            log.error("getTrashFiles error: {}", e.getMessage());
            throw new ApiException("Error fetching trash files");
        }
    }
}