package com.infotexa.userservice.model;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Organization {
    private Long startupId;
    private String startupUuid;
    private String name;
    private String role;
    private Long ownerId;
    private String createdAt;
}
