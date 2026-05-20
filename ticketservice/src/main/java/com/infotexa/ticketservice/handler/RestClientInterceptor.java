package com.infotexa.ticketservice.handler;

import com.infotexa.ticketservice.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class RestClientInterceptor implements ClientHttpRequestInterceptor {


    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        try {
            HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            var token = httpServletRequest.getHeader(AUTHORIZATION);
            request.getHeaders().add(AUTHORIZATION, token == null ? "" : token);
            return execution.execute(request, body);
        }catch (Exception exception){
            // log the error
            throw new ApiException("An error occurred while processing the request. Please try again.");
        }
    }
}
