package com.infotexa.storageservice.model;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StorageFileShare {

    private Long storageFileShareId;
    private Long storageFileId;
    private Long storageFolderId;
    private Long ownerId;
    private Long sharedWithUserId;
    private String permission;
    private String createdAt;
}