package com.infotexa.storageservice.service;

import com.infotexa.storageservice.domain.FileDownloadResult;
import com.infotexa.storageservice.dtoRequest.ShareRequest;
import com.infotexa.storageservice.model.StorageFile;
import com.infotexa.storageservice.model.StorageFolder;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

public interface StorageService {


    StorageFolder createFolder(String userUuid, String parentFolderUuid, String name);
    List<StorageFolder> getFolders(String folderUuid);
    StorageFolder getFolder(String folderUuid);
    List<StorageFile> uploadFile(String userUuid, String folderUuid,List<MultipartFile> files);
    List<StorageFile> getFiles(String folderUuid);
    Boolean deleteFile(String fileUuid);
    StorageFolder getRootFolder(String userUuid);
    FileDownloadResult downloadFile(String userUuid , String fileUuid);
    void shareFolder(String ownerUuid, ShareRequest request);
    void shareFile(String ownerUuid, ShareRequest request);
    List<StorageFolder> sharedFolders(String userUuid);
    List<StorageFile> sharedFiles(String userUuid);
}