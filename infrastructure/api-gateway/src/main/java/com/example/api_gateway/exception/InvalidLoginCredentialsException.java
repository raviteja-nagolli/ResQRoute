package com.example.api_gateway.exception;

public class InvalidLoginCredentialsException extends RuntimeException{
    public InvalidLoginCredentialsException(String message) {
        super(message);
    }
}
