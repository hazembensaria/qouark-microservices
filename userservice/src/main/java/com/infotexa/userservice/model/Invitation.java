package com.infotexa.userservice.model;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Invitation {
    private Long invitationId;
    private String invitationUuid;
    private String startupName;
    private String invitedBy;
    private String startupUuid;
    private String invitedEmail;
    private String role;
    private String status;
    private String createdAt;
}
