package com.infotexa.userservice.dtoRequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResetPasswordRequest {
    @NotEmpty(message = "user Id name must not be empty")
    private String userUuid;
    @NotEmpty(message = "token name must not be empty")
    private String token;
    @NotEmpty(message = "newPassword name must not be empty")
    private String password;
    @NotEmpty(message = "confirmPassword name must not be empty")
    private String confirmPassword;
}
