package com.infotexa.gateway.utils;

import com.infotexa.gateway.domain.Response;
import com.infotexa.gateway.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;

import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static java.time.LocalTime.now;
import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

public class RequestUtils {
    private static final BiConsumer<HttpServletResponse , Response> writeResponse = (servletResponse , response) ->{
        try{
        var outputSteam = servletResponse.getOutputStream();
        new ObjectMapper().writeValue(outputSteam , response);
        outputSteam.flush();
        }catch (Exception exception){
            throw new ApiException(exception.getMessage());
        }
    };

    public static void handleErrorResponse(HttpServletRequest request , HttpServletResponse response ,Exception exception){
    if(exception instanceof AccessDeniedException){
        var apiResponse = getErrorResponse(request , response , exception , FORBIDDEN);
        writeResponse.accept(response , apiResponse);
       } else if (exception instanceof InvalidBearerTokenException) {
        var apiResponse = getErrorResponse(request , response , exception , UNAUTHORIZED);
        writeResponse.accept(response , apiResponse);
     }else if (exception instanceof InsufficientAuthenticationException) {
        var apiResponse = getErrorResponse(request , response , exception , UNAUTHORIZED);
        writeResponse.accept(response , apiResponse);
    }else if (exception instanceof MismatchedInputException) {
        var apiResponse = getErrorResponse(request , response , exception , BAD_REQUEST);
        writeResponse.accept(response , apiResponse);
    }else if (exception instanceof DisabledException || exception instanceof LockedException || exception instanceof BadCredentialsException || exception instanceof CredentialsExpiredException || exception instanceof ApiException) {
        var apiResponse = getErrorResponse(request , response , exception , BAD_REQUEST);
        writeResponse.accept(response , apiResponse);
    }else{
        var apiResponse = getErrorResponse(request , response , exception , INTERNAL_SERVER_ERROR);
        writeResponse.accept(response , apiResponse);
    }
    }

    private static final BiFunction<Exception , HttpStatus , String> errorReason = (exception , httpStatus) -> {
        if(httpStatus.isSameCodeAs(FORBIDDEN)){
            return "yuo dont have enough permission";
        }
        if(httpStatus.isSameCodeAs(UNAUTHORIZED)){
            return exception.getMessage().contains("Jwt expired at") ? "your token is expired" : "you are not authenticated";
        }
        if(exception instanceof DisabledException || exception instanceof LockedException || exception instanceof BadCredentialsException || exception instanceof CredentialsExpiredException || exception instanceof ApiException){
            return exception.getMessage();
        }
        if(httpStatus.is5xxServerError()){
            return "something went wrong in the server";
        }else{
            return "something went wrong";
        }
    };

    private static Response getErrorResponse(HttpServletRequest request , HttpServletResponse response , Exception exception , HttpStatus status) {

        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(status.value());
        return new Response(now().toString() , status.value() , request.getRequestURI() , HttpStatus.valueOf(status.value()), errorReason.apply(exception , status), getRootCauseMessage(exception), Collections.emptyMap());
    }

}
