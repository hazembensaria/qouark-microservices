package com.infotexa.storageservice.model;


import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StorageStats {

    private Long usedSizeBytes;
    private Long maxSizeBytes;
    private double percent;
}
