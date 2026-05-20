package com.infotexa.authorizationserver.repository.implimentation;

import com.infotexa.authorizationserver.exception.ApiException;
import com.infotexa.authorizationserver.model.User;
import com.infotexa.authorizationserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.infotexa.authorizationserver.query.UserQuery.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JdbcClient jdbc;

    @Override
    public User getUserByUuid(String userUuid) {
       try {
            return jdbc.sql(SELECT_USER_BY_USER_UUID_QUERY)
                    .param("userUuid", userUuid)
                    .query(User.class)
                    .single();

       }catch (EmptyResultDataAccessException exception ){
           log.error(exception.getMessage());
           throw new ApiException(String.format("User with uuid %s not found", userUuid));
       } catch (Exception exception){
           log.error(exception.getMessage());
           throw new ApiException(String.format("User with uuid %s not found", userUuid));

       }
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            return jdbc.sql(SELECT_USER_BY_EMAIL_QUERY)
                    .param("email", email)
                    .query(User.class)
                    .single();

        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("User with email %s not found", email));
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error occurred while retrieving the user. Please try again later.");

        }
    }

    @Override
    public void resetLoginAttempts(String userUuid) {
        try {
            jdbc.sql(RESET_LOGIN_ATTEMPTS_QUERY)
                    .param("userUuid", userUuid)
                    .update();

        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("User with uuid %s not found", userUuid));
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException(String.format("User with uuid %s not found", userUuid));

        }
    }

    @Override
    public void updateLoginAttempts(String email) {
        try {
            jdbc.sql(UPDATE_LOGIN_ATTEMPTS_QUERY)
                    .param("email", email)
                    .update();

        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("User with uuid %s not found", email));
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException(String.format("User with uuid %s not found", email));

        }
    }

    @Override
    public void setLastLogin(Long userId) {
        try {
            jdbc.sql(SET_LAST_LOGIN_QUERY)
                    .param("userId", userId)
                    .update();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("User with uuid %s not found", userId));
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException(String.format("User with uuid %s not found", userId));

        }
    }

    @Override
    public void addLoginDevice(Long userId, String device, String client, String ipAddress) {
        try {
            jdbc.sql(INSERT_NEW_DEVICE_QUERY)
                    .params(Map.of("userId" , userId , "device" , device , "client" , client , "ipAddress" , ipAddress))
                    .update();
        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("User with uuid %s not found", userId));
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException(String.format("User with uuid %s not found", userId));

        }
    }


}
