package com.infotexa.userservice.model;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordToken {

    private Long passwordTokenId;
    private Long userId;
    private String token;
    private boolean expired;
    private String createdAt;
    private String expiresAt;
}
