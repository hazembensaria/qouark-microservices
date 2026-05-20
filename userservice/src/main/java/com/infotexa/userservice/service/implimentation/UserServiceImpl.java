package com.infotexa.userservice.service.implimentation;


import com.infotexa.userservice.event.Event;
import com.infotexa.userservice.exception.ApiException;
import com.infotexa.userservice.model.*;
import com.infotexa.userservice.repository.UserRepository;
import com.infotexa.userservice.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.infotexa.userservice.consatant.Constant.PHOTO_DIRECTORY;
import static com.infotexa.userservice.enumeration.EventType.RESETPASSWORD;
import static com.infotexa.userservice.enumeration.EventType.USER_CREATED;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Map.*;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang.WordUtils.capitalizeFully;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final ApplicationEventPublisher publisher;
    @Value("${ui.app.url}")
    private String uiAppUrl;



    @Override
    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    @Override
    public User getUserByUuid(String userUuid) {
        return userRepository.getUserByUuid(userUuid);
    }

    @Override
    public User updateUser(String UserUuid, String firstName, String lastName, String email, String phone, String bio, String address) {
        return userRepository.updateUser(UserUuid, firstName, lastName, email, phone, bio, address);
    }

    @Override
    public void createUser(String firstName, String lastName, String email, String username, String password) {
     var token = userRepository.createUser(firstName, lastName, email, username, encoder.encode(password));
     publisher.publishEvent(new Event(USER_CREATED, of( "token", token, "name" , capitalizeFully(firstName), "email" , email)));
    }

    @Override
    public void verifyAccount(String token) {
        var accountToken = userRepository.getAccountToken(token);
        if(!nonNull(accountToken)){
            throw new ApiException("Invalid link");
        }
        if(accountToken.isExpired()) {
            userRepository.deleteAccountToken(token);
            throw new ApiException("Link has expired. Please create your account again.");
        }
            userRepository.updateAccountSettings(accountToken.getUserId());
            userRepository.deleteAccountToken(token);
    }

    @Override
    public User verifyPasswordToken(String token) {
        var passwordToken = userRepository.getPasswordToken(token);
        if(!nonNull(passwordToken)){
            throw new ApiException("Invalid link");
        }
        if(passwordToken.isExpired()) {
            userRepository.deletePasswordToken(token);
            throw new ApiException("Link has expired. Please create your account again.");
        }
       return userRepository.getUserById(passwordToken.getUserId());
    }

    @Override
    public User enableMfa(String userUuid) {
        return userRepository.enableMfa(userUuid);
    }

    @Override
    public User disableMfa(String userUuid) {
        return userRepository.disableMfa(userUuid);
    }

    @Override
    public User uploadPhoto(String userUuid, MultipartFile file) {

        var user = userRepository.getUserByUuid(userUuid);
        var imageUrl = this.photoFunction.apply(user.getImageUrl(), file);
        userRepository.uploadImageUrl(userUuid , imageUrl);

        user.setImageUrl(imageUrl + "?timestamp=" + System.currentTimeMillis());
        return user;

    }

    @Override
    public User toggleAccountLocked(String userUuid) {

        return userRepository.toggleAccountLocked(userUuid);
    }

    @Override
    public User toggleAccountEnabled(String userUuid) {
        return userRepository.toggleAccountEnabled(userUuid);

    }

    @Override
    public User toggleAccountExpired(String userUuid) {
        return userRepository.toggleAccountExpired(userUuid);

    }

    @Override
    public User toggleCredentialsExpired(String userUuid) {
        return null;
    }

    @Override
    public void updatePassword(String userUuid, String currentPassword, String newPassword, String confirmNewPassword) {
         if(Objects.equals(confirmNewPassword, newPassword)){
             throw new ApiException("New password and confirm password do not match");
         }if(!encoder.matches(currentPassword, userRepository.getPassword(userUuid))){
             throw new ApiException("Current password is incorrect");
         }
         userRepository.updatePassword(userUuid, encoder.encode(newPassword));

    }

    @Override
    public User updateRole(String userUuid, String role) {
        return userRepository.updateRole(userUuid, role);

    }

    @Override
    public void resetPassword(String email) {
        var user = userRepository.getUserByEmail(email);
        var passwordToken = userRepository.getPasswordToken(user.getUserId());
        if(!nonNull(passwordToken)){
            var newToken = userRepository.createPasswordToken(user.getUserId());
           publisher.publishEvent(new Event(RESETPASSWORD, of( "token", newToken, "email" ,email , "name" , capitalizeFully(user.getFirstName()))));
        }else if(passwordToken.isExpired()){
            userRepository.deletePasswordToken(user.getUserId());
            var newToken = userRepository.createPasswordToken(user.getUserId());
          publisher.publishEvent(new Event(RESETPASSWORD, of( "token", newToken, "email" ,email , "name" , capitalizeFully(user.getFirstName()))));
        }else {
         publisher.publishEvent(new Event(RESETPASSWORD, of( "token", passwordToken.getToken(), "email" ,email , "name" , capitalizeFully(user.getFirstName()))));

        }

    }

    @Override
    public void doResetPassword(String Uuid, String token, String password, String confirmPassword) {
        if(!Objects.equals(password, confirmPassword)){
            throw new ApiException("New password and confirm password do not match");
        }
        var user = userRepository.getUserByUuid(Uuid);
        var passwordToken = userRepository.getPasswordToken(token);
        if(!Objects.equals(passwordToken.getUserId(), user.getUserId())){
            throw new ApiException("Invalid token");
        }
        userRepository.updatePassword(Uuid, encoder.encode(password));
        userRepository.deletePasswordToken(user.getUserId());
    }

    @Override
    public List<User> getUsers() {
        return userRepository.getUsers();
    }

    @Override
    public User getAssignee(String ticketUuid) {
        return userRepository.getAssignee(ticketUuid);
    }

    @Override
    public Credential getCredential(String userUuid) {
        return userRepository.getCredential(userUuid);
    }

    @Override
    public List<Device> geDevices(String userUuid) {
        return userRepository.geDevices(userUuid);
    }

    @Override
    public List<User> getTechSupports() {
        return userRepository.getTechSupports();
    }


    private final Function<String, String> fileExtension = name -> Optional.ofNullable(name)
            .filter(n -> n.contains("."))
            .map(n -> "." + n.substring(n.lastIndexOf('.') + 1))
            .orElse(".png");

    private final BiFunction<String, MultipartFile, String> photoFunction = (imageUrl, image) -> {
        try {
            String extension = fileExtension.apply(image.getOriginalFilename());
            String fileName = UUID.randomUUID() + extension; // fresh name each upload

            Path fileStorageLocation = Paths.get(PHOTO_DIRECTORY).toAbsolutePath().normalize();
            if (!Files.exists(fileStorageLocation)) Files.createDirectories(fileStorageLocation);

            // delete old image if exists
            if (imageUrl != null && !imageUrl.isBlank()) {
                String oldName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
                Path oldImage = fileStorageLocation.resolve(oldName).normalize();
                Files.deleteIfExists(oldImage);
            }

            Files.copy(image.getInputStream(), fileStorageLocation.resolve(fileName), REPLACE_EXISTING);

            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/user/image/" + fileName)
                    .toUriString();

        } catch (Exception e) {
            log.error("Failed to upload image: {}", e.getMessage(), e);
            throw new ApiException("Failed to upload image");
        }
    };

    public List<User> searchUsers(String query) {
        return userRepository.searchUsers(query);
    }

    @Override
    public Organization createOrganization(String userUuid, String name) {
        return userRepository.createOrganization(userUuid, name);
    }

    @Override
    public Organization getStartup(String userUuid) {
        return userRepository.getStartup( userUuid);
    }

    public Invitation inviteUser(String inviterUuid, String startupUuid, String email, String role) {
        String invitationUuid = UUID.randomUUID().toString();
        return userRepository.createInvitation(
                startupUuid,
                email,
                inviterUuid,
                role,
                invitationUuid
        );
    }

    public List<Invitation> getMyInvitations(String userUuid) {
        return userRepository.getMyInvitations(userUuid);
    }

    @Override
    public Invitation acceptInvitation(String invitationUuid,String userUuid) {
        return userRepository.acceptInvitation(invitationUuid,userUuid);
    }

}
