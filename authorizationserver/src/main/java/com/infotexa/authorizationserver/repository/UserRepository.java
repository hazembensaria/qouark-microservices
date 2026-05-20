package com.infotexa.authorizationserver.repository;

import com.infotexa.authorizationserver.model.User;

public interface UserRepository {

    User getUserByUuid(String userUuid);
    User getUserByEmail(String email) ;
    void resetLoginAttempts(String userUuid);
    void updateLoginAttempts(String email);
    void setLastLogin(Long userId);
    void addLoginDevice(Long userId , String devicName , String client , String ipAddress);

}
