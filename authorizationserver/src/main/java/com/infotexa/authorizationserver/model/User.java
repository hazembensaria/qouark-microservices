package com.infotexa.authorizationserver.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long userId;
    private String userUuid;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String phone;
    private String bio;
    private String imageUrl;
    private String qrCodeSecret;
    private String qrCodeImageUri;
    private String lastLogin;
    private int loginAttempts;
    private String createdAt;
    private String updateAt;
    private String role;
    private String authorities;
    private boolean mfa;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

}
