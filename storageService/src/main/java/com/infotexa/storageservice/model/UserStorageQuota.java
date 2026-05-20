package com.infotexa.storageservice.model;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserStorageQuota {

   private Long userStorageQuotaId;
   private Long userId;
   private Long maxSizeBytes;
   private Long usedSizeBytes;
   private String createdAt;
   private String updatedAt;
}