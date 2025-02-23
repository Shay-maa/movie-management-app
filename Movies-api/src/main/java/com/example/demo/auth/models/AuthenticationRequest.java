package com.example.demo.auth.models;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationRequest {
    @NotEmpty(message = "Password cannot be Empty")
    @NotBlank(message = "Password cannot be Empty")
    private String password ;

    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email cannot be Empty")
    @NotBlank(message = "Email cannot be Empty")
    private String email ;
}
