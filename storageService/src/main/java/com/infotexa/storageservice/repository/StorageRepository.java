package com.infotexa.storageservice.repository;

import com.infotexa.storageservice.model.StorageFile;
import com.infotexa.storageservice.model.StorageFolder;

import java.util.List;

public interface StorageRepository {


    StorageFolder createFolder(String userUuid, String parentFolderUuid, String folderUuid, String name);
    List<StorageFolder> getFolders(String folderUuid);
    StorageFolder getFolderByUuid(String folderUuid);
    StorageFile saveFile(String fileUuid, String userUuid, String folderUuid, String name, String extension, Long size, String formattedSize, String storagePath, String uri);
    List<StorageFile> getFiles(String folderUuid);
    Boolean deleteFile(String fileUuid);
    String createPublicLink(String publicLinkUuid, String fileUuid, String token, String userUuid);
    boolean checkQuota(String userUuid, long fileSize);
    StorageFolder getRootFolder(String userUuid);
    StorageFile getStorageFile(String userUuid, String fileUuid);
    void shareFolder(String ownerUuid, String folderUuid, String sharedWithUserUuid, String permission);
    void shareFile(String ownerUuid, String fileUuid, String sharedWithUserUuid, String permission);
    List<StorageFolder> sharedFolders(String userUuid);
    List<StorageFile> sharedFiles(String userUuid);
}