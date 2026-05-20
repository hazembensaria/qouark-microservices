package com.infotexa.authorizationserver.service;

import com.infotexa.authorizationserver.model.User;

public interface UserService {

    User getUserByEmail(String email) ;
    void resetLoginAttempts(String userUuid);
    void updateLoginAttempts(String email);
    void setLastLogin(Long userId);
    void addLoginDevice(Long userId , String devicName , String client , String ipAddress);
    boolean verifyQrCode (String userUuid , String code);
}
