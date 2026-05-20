package com.infotexa.notificationservice.exception;

import com.infotexa.notificationservice.domain.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

import static com.infotexa.notificationservice.utils.RequestUtils.handleErrorResponse;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class HandleException extends ResponseEntityExceptionHandler implements ErrorController {

    private final HttpServletRequest request;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest webRequest) {

        log.error(String.format("MethodArgumentNotValidException: %s", exception.getMessage()));

        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        String fieldMessage = fieldErrors.stream()
                .map(fieldError -> fieldError.getField() + ": " +
                        (fieldError.getDefaultMessage() == null ? "invalid" : fieldError.getDefaultMessage()))
                .collect(Collectors.joining(", "));

        if (fieldMessage.isBlank()) {
            fieldMessage = "Validation failed";
        }

        return new ResponseEntity<>(
                handleErrorResponse(fieldMessage, getRootCauseMessage(exception), request, BAD_REQUEST),
                BAD_REQUEST
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Response> badRequestException(BadRequestException exception) {
        return new ResponseEntity<>(
                handleErrorResponse(exception.getMessage(), getRootCauseMessage(exception), request, BAD_REQUEST),
                BAD_REQUEST
        );
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Response> forbiddenException(ForbiddenException exception) {
        return new ResponseEntity<>(
                handleErrorResponse(exception.getMessage(), getRootCauseMessage(exception), request, FORBIDDEN),
                FORBIDDEN
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response> accessDeniedException(AccessDeniedException exception) {
        return new ResponseEntity<>(
                handleErrorResponse("Access denied", getRootCauseMessage(exception), request, FORBIDDEN),
                FORBIDDEN
        );
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException exception,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest webRequest) {
        return new ResponseEntity<>(
                handleErrorResponse(
                        "No handler found for " + exception.getHttpMethod() + " " + exception.getRequestURL(),
                        getRootCauseMessage(exception),
                        request,
                        NOT_FOUND
                ),
                NOT_FOUND
        );
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Response> apiException(ApiException exception) {
        return new ResponseEntity<>(
                handleErrorResponse(exception.getMessage(), getRootCauseMessage(exception), request, BAD_REQUEST),
                BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> exception(Exception exception) {
        return new ResponseEntity<>(
                handleErrorResponse(processErrorMessage(exception), getRootCauseMessage(exception), request, INTERNAL_SERVER_ERROR),
                INTERNAL_SERVER_ERROR
        );
    }

    private String processErrorMessage(Exception exception) {
        if (exception.getMessage() != null) {
            String msg = exception.getMessage();

            if (msg.contains("duplicate") && msg.contains("AccountVerifications")) {
                return "You already verified your account.";
            }

            if (msg.contains("duplicate") && msg.contains("ResetPasswordVerifications")) {
                return "We already sent you an email to reset your password.";
            }

            if (msg.contains("duplicate") && msg.contains("Key (email)")) {
                return "Email already exists. Use a different email and try again.";
            }

            if (msg.contains("duplicate")) {
                return "Duplicate entry. Please try again.";
            }
        }

        return "An error occurred. Please try again.";
    }

    private String getRootCauseMessage(Throwable throwable) {
        Throwable root = throwable;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        return root.getMessage() != null ? root.getMessage() : throwable.getClass().getSimpleName();
    }
}