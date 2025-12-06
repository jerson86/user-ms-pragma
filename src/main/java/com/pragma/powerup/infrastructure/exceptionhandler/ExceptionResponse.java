package com.pragma.powerup.infrastructure.exceptionhandler;

import lombok.Getter;

@Getter
public enum ExceptionResponse {
    NO_DATA_FOUND("No data found for the requested petition");

    private final String message;

    ExceptionResponse(String message) {
        this.message = message;
    }

}