package com.infotexa.storageservice.dtoRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShareRequest {
    private String resourceUuid;
    private String sharedWithUserUuid;
    private String sharedWithUserEmail;
    private String permission;
}