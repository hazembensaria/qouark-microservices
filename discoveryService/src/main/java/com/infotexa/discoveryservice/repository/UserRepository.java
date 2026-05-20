package com.infotexa.discoveryservice.repository;


import com.infotexa.discoveryservice.model.User;

public interface UserRepository {
    User getUserByUsername(String username);
}
