package com.infotexa.authorizationserver.service.implimentation;

import com.infotexa.authorizationserver.model.User;
import com.infotexa.authorizationserver.repository.UserRepository;
import com.infotexa.authorizationserver.service.UserService;
import com.infotexa.authorizationserver.utils.UserUtils;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.infotexa.authorizationserver.utils.UserUtils.verifyCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    @Override
    public void resetLoginAttempts(String userUuid) {
        userRepository.resetLoginAttempts(userUuid);

    }

    @Override
    public void updateLoginAttempts(String email) {
        userRepository.updateLoginAttempts(email);
    }

    @Override
    public void setLastLogin(Long userId) {
        userRepository.setLastLogin(userId);
    }

    @Override
    public void addLoginDevice(Long userId, String devicName, String client, String ipAddress) {
        userRepository.addLoginDevice(userId, devicName, client, ipAddress);
    }

    @Override
    public boolean verifyQrCode(String userId, String code) {
        var user = userRepository.getUserByUuid(userId);
        return verifyCode(user.getQrCodeSecret(), code);
    }
}
