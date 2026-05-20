package com.infotexa.ticketservice.dtoRequest;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRequest {
    @NotEmpty(message = "First name must not be empty")
    private String firstName;
    @NotEmpty(message = "Last name must not be empty")
    private String lastName;
    @NotEmpty(message = "Email must not be empty")
    private String email;
    @NotEmpty(message = "Username must not be empty")
    private String username;
    @NotEmpty(message = "Password must not be empty")
    private String password;
    private String phone;
    private String bio;
    private String address;
}
