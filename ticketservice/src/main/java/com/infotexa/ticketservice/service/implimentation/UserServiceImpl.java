package com.infotexa.ticketservice.service.implimentation;

import com.infotexa.ticketservice.domain.Response;
import com.infotexa.ticketservice.exception.ApiException;
import com.infotexa.ticketservice.handler.RestClientInterceptor;
import com.infotexa.ticketservice.model.Ticket;
import com.infotexa.ticketservice.model.User;
import com.infotexa.ticketservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

import static com.infotexa.ticketservice.utils.RequestUtils.convertResponse;
import static com.infotexa.ticketservice.utils.RequestUtils.convertResponseList;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final RestClient restClient;

    public UserServiceImpl() {
        this.restClient = RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl("http://user-service:8085")
                .requestInterceptor(new RestClientInterceptor())
//                .requestInitializer(null)
                .build();
    }

    @Override
    public User getTicketUser(String ticketUuid) {
        try {
            var response =  restClient.get()
                    .uri("/user/assignee/" + ticketUuid)
                    .retrieve()
                    .body(Response.class);
            return convertResponse(response , User.class , "user");
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");
        }
    }

    @Override
    public User getUserByUuid(String userUuid) {
        try {
            var response =  restClient.get()
                    .uri("/user/profile")
                    .retrieve()
                    .body(Response.class);
            return convertResponse(response , User.class , "user");
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");
        }
    }

    @Override
    public User getAssignee(String ticketUuid) {
        try {
            var response =  restClient.get()
                    .uri("/user/assignee/" + ticketUuid)
                    .retrieve()
                    .body(Response.class);
            return convertResponse(response , User.class , "user");
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");
        }
    }


    @Override
    public List<User> getTechSupports() {
        try {
            var response =  restClient.get()
                    .uri("/user/techsupports")
                    .retrieve()
                    .body(Response.class);
            return convertResponseList(response , User.class , "techSupports");
        } catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An occurred. Please try again.");
        }
    }

}
