package com.infotexa.storageservice.model;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StorageFile {

    private Long storageFileId;
    private String storageFileUuid;
    private Long ownerId;
    private Long storageFolderId;
    private String storagePath;
    private String extension;
    private String formattedSize;
    private String name;
    private Long size;
    private String uri;
    private Boolean isPublic;
    private String createdAt;
    private String updatedAt;
}