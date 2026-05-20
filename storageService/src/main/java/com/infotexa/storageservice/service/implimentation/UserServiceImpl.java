package com.infotexa.storageservice.service.implimentation;


import com.infotexa.storageservice.domain.Response;
import com.infotexa.storageservice.exception.ApiException;
import com.infotexa.storageservice.handler.RestClientInterceptor;
import com.infotexa.storageservice.model.User;
import com.infotexa.storageservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

import static com.infotexa.storageservice.utils.RequestUtils.convertResponse;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final RestClient restClient;

    public UserServiceImpl() {
        this.restClient = RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl("http://localhost:8085")
                .requestInterceptor(new RestClientInterceptor())
//                .requestInitializer(null)
                .build();
    }

    @Override
    public User getUserByUuid(String userUuid) {
        try {
            var response =  restClient.get()
                    .uri("/user/profile")
                    .retrieve()
                    .body(Response.class);
            return null ;
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");
        }
    }
}
