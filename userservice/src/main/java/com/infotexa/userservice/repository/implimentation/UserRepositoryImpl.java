package com.infotexa.userservice.repository.implimentation;


import com.infotexa.userservice.exception.ApiException;
import com.infotexa.userservice.model.*;
import com.infotexa.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.infotexa.userservice.query.UserQuery.*;
import static com.infotexa.userservice.utils.UserUtils.*;
import static java.util.Locale.of;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JdbcClient jdbc;


    @Override
    public User getUserByEmail(String email) {
        try {
           return jdbc.sql(SELECT_USER_BY_EMAIL_QUERY)
                    .param( "email" , email)
                    .query(User.class).single();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("User with email %s not found", email));
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");

        }
    }

    @Override
    public User getUserByUuid(String userUuid) {
        try {
            return jdbc.sql(SELECT_USER_BY_USER_UUID_QUERY)
                    .param( "userUuid" , userUuid)
                    .query(User.class).single();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("User with uuid %s not found", userUuid));
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");

        }
    }

    @Override
    public User getUserById(Long userId) {
        try {
            return jdbc.sql(SELECT_USER_BY_USER_ID_QUERY)
                    .param( "userId" , userId)
                    .query(User.class).single();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("User with uuid %s not found", userId));
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");

        }
    }

    @Override
    public User updateUser(String userUuid, String firstName, String lastName, String email, String phone, String bio, String address) {
        try {
            return jdbc.sql(UPDATE_USER_FUNCTION)
                    .params( Map.of("userUuid" , userUuid , "firstName" , firstName , "lastName" , lastName , "email" , email , "phone" , phone , "bio" , bio , "address" , address))
                    .query(User.class).single();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("User with uuid %s not found", userUuid));
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");

        }
    }

    @Override
    public String createUser(String firstName, String lastName, String email, String username, String password) {
        try {
            var token = UUID.randomUUID().toString();
             jdbc.sql(CREATE_USER_PROCEDURE)
                    .params( Map.of( "userUuid" , randomUUID.get(),"firstName" , firstName , "lastName" , lastName , "email" , email.trim().toLowerCase() , "username" , username.trim().toLowerCase() , "password" , password , "credentialUuid", randomUUID.get() , "token" , token ,"memberId" , memberId.get() ))
                    .update();
            return token;
        }catch (DuplicateKeyException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("User with email %s already exists", email));
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");

        }
    }

    @Override
    public AccountToken getAccountToken(String token) {
        try {
            return jdbc.sql(SELECT_ACCOUNT_TOKEN_QUERY)
                    .param("token" , token)
                    .query(AccountToken.class).single();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException("Invalid link");
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");

        }

    }

    @Override
    public User verifyPasswordToken(String token) {
        return null;
    }

    @Override
    public User enableMfa(String userUuid) {
        try {
            return jdbc.sql(ENABLE_USER_MFA_FUNCTION)
                    .paramSource(getParamSource(userUuid , qrCodeSecret.get()))
                    .query(User.class).single();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException("User not found. please try again.");
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");

        }
    }

    @Override
    public User disableMfa(String userUuid) {
        try {
            return jdbc.sql(DISABLE_USER_MFA_FUNCTION)
                    .param("userUuid" , userUuid)
                    .query(User.class).single();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException("User not found. please try again.");
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");

        }
    }


    @Override
    public User toggleAccountLocked(String userUuid) {
        try {
            return jdbc.sql(TOGGLE_ACCOUNT_LOCKED_FUNCTION)
                    .param("userUuid" , userUuid)
                    .query(User.class).single();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException("User not found. please try again.");
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");

        }
    }

    @Override
    public User toggleAccountEnabled(String userUuid) {
        try {
            return jdbc.sql(TOGGLE_ACCOUNT_ENABLED_FUNCTION)
                    .param("userUuid" , userUuid)
                    .query(User.class).single();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException("User not found. please try again.");
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");

        }
    }

    @Override
    public User toggleAccountExpired(String userUuid) {
        try {
            return jdbc.sql(TOGGLE_ACCOUNT_EXPIRED_FUNCTION)
                    .param("userUuid" , userUuid)
                    .query(User.class).single();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException("User not found. please try again.");
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");

        }
    }

    @Override
    public User toggleCredentialsExpired(String userUuid) {
        return null;
    }

    @Override
    public void updatePassword(String userUuid, String encodedPassword) {
        try {
            jdbc.sql(UPDATE_USER_PASSWORD_QUERY)
                    .params( Map.of("userUuid" , userUuid , "encodedPassword" , encodedPassword))
                    .update();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException("User not found. please try again.");
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");

        }
    }

    @Override
    public User updateRole(String userUuid, String role) {
        try {
            log.info("Calling function with userUuid={}, role={}", userUuid, role);
            return jdbc.sql(UPDATE_USER_ROLE_FUNCTION)
                     .params(Map.of("userUuid" , userUuid , "role" , role))
                    .query(User.class).single();
        } catch (EmptyResultDataAccessException ex) {
            log.warn("No user found for userUuid={} and role={}", userUuid, role);
            throw new ApiException("User not found. Please try again.");
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");

        }
    }

    @Override
    public void resetPassword(String email) {

    }

    @Override
    public void doResetPassword(String Uuid, String token, String newPassword, String confirmPassword) {

    }

    @Override
    public List<User> getUsers() {
       try {
           return jdbc.sql(SELECT_USERS_QUERY)
                   .query(User.class).list();
       }catch (Exception exception){
           throw new ApiException("An occurred. Please try again.");
       }
    }

    @Override
    public User getAssignee(String ticketUuid) {
        try {
            return jdbc.sql(SELECT_ASSIGNEE_BY_TICKET_UUID_QUERY)
                    .param("ticketUuid" , ticketUuid)
                    .query(User.class).single();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            log.error("Ticket is not assigned");
            return User.builder().build();
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");
        }

    }

    @Override
    public Credential getCredential(String userUuid) {
        try {
            return jdbc.sql(SELECT_USER_CREDENTIAL_QUERY)
                    .param("userUuid" , userUuid)
                    .query(Credential.class).single();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException("User not found. please try again.");
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");

        }
    }

    @Override
    public List<Device> geDevices(String userUuid) {
        try {
            return jdbc.sql(SELECT_USER_DEVICES_QUERY)
                    .param("userUuid" , userUuid)
                    .query(Device.class).list();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException("User not found. please try again.");
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");

        }
    }

    @Override
    public void deleteAccountToken(String token) {
        try {
             jdbc.sql(DELETE_ACCOUNT_TOKEN_QUERY)
                    .param("token" , token)
                    .update();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException("Token not found");
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");

        }
    }

    @Override
    public void updateAccountSettings(Long userId) {
        try {
            jdbc.sql(UPDATE_ACCOUNT_SETTINGS_QUERY)
                    .param("userId" , userId)
                    .update();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException("user not found");
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");

        }
    }

    @Override
    public PasswordToken getPasswordToken(String token) {
        try {
            return jdbc.sql(SELECT_PASSWORD_TOKEN_QUERY)
                    .param("token" , token)
                    .query(PasswordToken.class).single();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException("Invalid link");
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");

        }
    }

    @Override
    public PasswordToken getPasswordToken(Long userId) {
        try {
            return jdbc.sql(SELECT_PASSWORD_TOKEN_BY_USERID_QUERY)
                    .param("userId" , userId)
                    .query(PasswordToken.class).single();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            return null ;
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");

        }
    }

    @Override
    public void deletePasswordToken(String token) {
        try {
            jdbc.sql(DELETE_PASSWORD_TOKEN_QUERY)
                    .param("token" , token)
                    .update();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException("Token not found");
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");

        }
    }

    @Override
    public void deletePasswordToken(Long userId) {
        try {
            jdbc.sql(DELETE_PASSWORD_TOKEN_BY_USERID_QUERY)
                    .param("userId" , userId)
                    .update();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException("Token not found");
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");

        }
    }

    @Override
    public void uploadImageUrl(String userUuid, String imageUrl) {
        try {
             jdbc.sql(UPDATE_USER_IMAGE_URL_QUERY)
                    .params( Map.of("userUuid" , userUuid , "imageUrl" , imageUrl))
                    .update();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException("Invalid link");
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");

        }
    }

    @Override
    public String getPassword(String userUuid) {
        try {
            return jdbc.sql(SELECT_USER_PASSWORD_QUERY)
                    .param("userUuid" , userUuid)
                    .query(String.class).single();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException("User not found. please try again.");
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");

        }
    }

    @Override
    public String createPasswordToken(Long userId) {
        try {
            var token = UUID.randomUUID().toString();
             jdbc.sql(CREATE_PASSWORD_TOKEN_QUERY)
                    .params( Map.of("userId" , userId , "token" , token))
                    .update();
            return token;
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException("User not found. please try again.");
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");

        }
    }

    @Override
    public List<User> getTechSupports() {
        try {
               return jdbc.sql(SELECT_TECH_SUPPORTS_QUERY)
                        .query(User.class).list();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException("Users not found. please try again.");
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");

        }
    }

    @Override
    public List<User> searchUsers(String query) {
        try {
            return jdbc.sql(SEARCH_USERS_QUERY)
                    .params( Map.of("query" , query.trim().toLowerCase()))
                    .query(User.class).list();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException("Users not found. please try again.");
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");

        }
    }


    private SqlParameterSource getParamSource(String userUuid , String qrCodeSecret){
        return new MapSqlParameterSource()
                .addValue("userUuid" , userUuid)
                .addValue("qrCodeSecret" , qrCodeSecret)
                .addValue("qrCodeImageUri" , qrCodeImageUri.apply(qrCodeSecret));
    }

    @Override
    public Organization createOrganization(String userUuid, String name) {
        try {
            return jdbc.sql(CREATE_ORGANIZATION_FUNCTION)
                    .params(Map.of(
                            "userUuid", userUuid,
                            "name", name,
                            "startupUuid", UUID.randomUUID().toString()
                    ))
                    .query(Organization.class)
                    .single();
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("Organization not found");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public Organization getStartup( String userUuid) {
        try {
            return jdbc.sql(GET_STARTUP)
                    .params(Map.of("userUuid", userUuid))
                    .query(Organization.class)
                    .single();
        } catch (EmptyResultDataAccessException e) {
            throw new ApiException("Startup not found or user not a member");
        }
    }

    @Override
    public Invitation createInvitation(String startupUuid, String email, String inviterUuid, String role, String invitationUuid) {
        try {
            return jdbc.sql(CREATE_INVITATION_FUNCTION)
                    .params(Map.of(
                            "startupUuid", startupUuid,
                            "invitedEmail", email,
                            "invitedByUuid", inviterUuid,
                            "role", role,
                            "invitationUuid", invitationUuid
                    ))
                    .query(Invitation.class)
                    .single();

        } catch (EmptyResultDataAccessException e) {
            throw new ApiException("Cannot create invitation");
        } catch (Exception e) {
            throw new ApiException("Unexpected error while creating invitation");
        }
    }
    @Override
    public List<Invitation> getMyInvitations(String userUuid) {
        try {
            return jdbc.sql(GET_MY_INVITATIONS_QUERY)
                    .params(Map.of("userUuid", userUuid))
                    .query(Invitation.class)
                    .list();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("Failed to fetch invitations");
        }
    }

    @Override
    public Invitation acceptInvitation(String invitationUuid, String userUuid) {

        try {

            return jdbc.sql(ACCEPT_INVITATION_FUNCTION)
                    .params(Map.of(
                            "invitationUuid", invitationUuid,
                            "userUuid", userUuid
                    ))
                    .query(Invitation.class)
                    .single();

        } catch (Exception e) {

            log.error(e.getMessage());

            throw new ApiException("Failed to accept invitation");
        }
    }
}
