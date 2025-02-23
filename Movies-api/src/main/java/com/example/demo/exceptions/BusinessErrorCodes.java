package com.example.demo.exceptions;


import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum BusinessErrorCodes {
    NO_CODE(0, NOT_IMPLEMENTED , "No Code"),
    INCORRECT_CURRENT_PASSWORD(300, BAD_REQUEST , "Current password is incorrect"),
    NEW_PASSWORD_DOES_NOT_MATCH(301, BAD_REQUEST , "The new password does not match"),
    BAD_CREDENTIALS(304, FORBIDDEN , "Username or password is incorrect"),

    ;


    @Getter
    private final int code;
    @Getter
    private final HttpStatus httpStatus;
    @Getter
    private final String description;



    BusinessErrorCodes(int code, HttpStatus httpStatus, String description) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.description = description;
    }
}

