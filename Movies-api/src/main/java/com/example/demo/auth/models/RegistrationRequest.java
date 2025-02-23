package com.example.demo.auth.models;


import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequest {

    @NotEmpty(message = "First name cannot be Empty")
    @NotBlank(message = "First name cannot be Empty")
    private String firstName ;

    @NotEmpty(message = "Last name cannot be Empty")
    @NotBlank(message = "Last name cannot be Empty")
    private String lastName ;

    @Size(min = 8, message = "password should be 8 characters long minimum")
    @NotEmpty(message = "Password cannot be Empty")
    @NotBlank(message = "Password cannot be Empty")
    private String password ;

    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email cannot be Empty")
    @NotBlank(message = "Email cannot be Empty")
    private String email ;
}
