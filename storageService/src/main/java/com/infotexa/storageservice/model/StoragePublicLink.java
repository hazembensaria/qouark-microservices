package com.infotexa.storageservice.model;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoragePublicLink {

    private Long storagePublicLinkId;
    private String publicLinkUuid;
    private Long storageFileId;
    private Long storageFolderId;
    private String token;
    private String expiresAt;
    private Long createdByUserId;
    private String createdAt;
}