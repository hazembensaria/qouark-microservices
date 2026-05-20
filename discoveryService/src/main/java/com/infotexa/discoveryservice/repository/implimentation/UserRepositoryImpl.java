package com.infotexa.discoveryservice.repository.implimentation;

import com.infotexa.discoveryservice.exception.ApiException;
import com.infotexa.discoveryservice.model.User;
import com.infotexa.discoveryservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import static com.infotexa.discoveryservice.query.UserQuery.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JdbcClient jdbc;

    @Override
    public User getUserByUsername(String username) {
        try {
            return jdbc.sql(SELECT_USER_BY_USERNAME_QUERY)
                    .param("username", username)
                    .query(User.class)
                    .single();

        }catch (EmptyResultDataAccessException exception ){
            log.error(exception.getMessage());
            throw new ApiException(String.format("User with uuid %s not found", username));
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException(String.format("User with uuid %s not found", username));

        }
    }


}
