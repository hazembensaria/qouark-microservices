package com.infotexa.userservice.service;


import com.infotexa.userservice.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    User getUserByEmail(String email) ;
    User getUserByUuid(String userUuid);
    User updateUser(String UserUuid ,String firstName , String lastName , String email , String phone , String bio , String address);
    void createUser(String firstName , String lastName ,String email , String username , String password );
    void verifyAccount(String token);
    User verifyPasswordToken(String token);
    User enableMfa(String userUuid);
    User disableMfa(String userUuid);
    User uploadPhoto(String userUuid , MultipartFile file);
    User toggleAccountLocked(String userUuid);
    User toggleAccountEnabled(String userUuid);
    User toggleAccountExpired(String userUuid);
    User toggleCredentialsExpired(String userUuid);
    void updatePassword(String userUuid , String currentPassword , String newPassword , String confirmNewPassword);
    User updateRole(String userUuid , String role);
    void resetPassword(String email);
    void doResetPassword(String Uuid , String token , String password , String confirmPassword);
    List<User> getUsers();
    User getAssignee(String ticketUuid);
    Credential getCredential(String userUuid);
    List<Device> geDevices(String userUuid);
    List<User> getTechSupports();
    List<User> searchUsers(String query);
    Organization createOrganization(String userUuid, String name);
    Organization getStartup(String userUuid);
    Invitation inviteUser(String inviterUuid, String startupUuid, String email, String role);
    List<Invitation> getMyInvitations(String userUuid) ;
    Invitation acceptInvitation(String invitationUuid,String userUuid);
}
