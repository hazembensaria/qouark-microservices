package com.infotexa.storageservice.model;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StorageFolder {

        private Long storageFolderId;
        private String storageFolderUuid;
        private Long ownerId;
        private Long parentFolderId;
        private String name;
        private String path;
        private Boolean isRoot;
        private String createdAt;
        private String updatedAt;
}