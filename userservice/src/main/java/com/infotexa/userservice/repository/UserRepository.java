package com.infotexa.userservice.repository;


import com.infotexa.userservice.model.*;
import org.jspecify.annotations.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserRepository {

    User getUserByEmail(String email) ;
    User getUserByUuid(String userUuid);
    User getUserById(Long userId);
    User updateUser(String userUuid ,String firstName , String lastName , String email , String phone , String bio , String address);
    String createUser(String firstName , String lastName ,String email , String username , String password );
    AccountToken getAccountToken(String token);
    User verifyPasswordToken(String token);
    User enableMfa(String userUuid);
    User disableMfa(String userUuid);
    User toggleAccountLocked(String userUuid);
    User toggleAccountEnabled(String userUuid);
    User toggleAccountExpired(String userUuid);
    User toggleCredentialsExpired(String userUuid);
    void updatePassword(String userUuid , String encodedPassword);
    User updateRole(String userUuid , String role);
    void resetPassword(String email);
    void doResetPassword(String Uuid , String token , String newPassword , String confirmPassword);
    List<User> getUsers();
    User getAssignee(String ticketUuid);
    Credential getCredential(String userUuid);
    List<Device> geDevices(String userUuid);
    void deleteAccountToken(String token);
    void updateAccountSettings(Long userId);
    PasswordToken getPasswordToken(String token);
    PasswordToken getPasswordToken(Long userId);
    void deletePasswordToken(String token);
    void deletePasswordToken(Long userId);
    void uploadImageUrl(String userUuid, String imageUrl);
    String getPassword(String userUuid);
    String createPasswordToken(Long userId);
    List<User> getTechSupports();
    List<User> searchUsers(String query);
    Organization createOrganization(String userUuid, String name);
    Organization getStartup( String userUuid);
    Invitation createInvitation(String startupUuid, String email, String inviterUuid, String role, String invitationUuid);
    List<Invitation> getMyInvitations(String userUuid) ;
    Invitation acceptInvitation(String invitationUuid, String userUuid);
}
