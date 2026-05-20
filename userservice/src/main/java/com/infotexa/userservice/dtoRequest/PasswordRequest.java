package com.infotexa.userservice.dtoRequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PasswordRequest {
    @NotEmpty(message = "currentPassword name must not be empty")
    private String currentPassword;
    @NotEmpty(message = "newPassword name must not be empty")
    private String newPassword;
    @NotEmpty(message = "confirmPassword name must not be empty")
    private String confirmNewPassword;

}
