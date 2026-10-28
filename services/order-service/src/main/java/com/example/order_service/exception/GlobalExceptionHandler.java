package com.example.order_service.exception;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<Map<String,Object>> handleFeignNotFound(FeignException.NotFound exception){
        Map<String, Object> response=new HashMap<>();
        response.put("status",404);
        response.put("message","Product not found");
        response.put("errors",new HashMap<>());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException exception){
        ErrorResponse errorResponse=new ErrorResponse();
        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponse.setMessage(exception.getMessage());
        errorResponse.setErrors(new HashMap<>());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(InvalidOrderStatusTransitionException.class)
    public ErrorResponse handleInvalidTransition(InvalidOrderStatusTransitionException exception){
        ErrorResponse response=new ErrorResponse();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage(exception.getMessage());
        response.setErrors(new HashMap<>());
        return response;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleInvalidInput(MethodArgumentNotValidException exception){
        ErrorResponse errorResponse=new ErrorResponse();
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage("Validation failed");
        Map<String, String> errors=new HashMap<>();
        for (FieldError error:exception.getBindingResult().getFieldErrors()){
            errors.put(error.getField(),error.getDefaultMessage());
        }
        errorResponse.setErrors(errors);
        return errorResponse;
    }
}
