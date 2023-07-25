package com.example.ambulanceserviceprovider.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.UUID;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<AppExceptionResponse> CustomException (CustomException e) {
        AppExceptionResponse exceptionResponse = AppExceptionResponse.builder()
                .requestTime(LocalDateTime.now())
                .requestType("Outbound")
                .referenceId(UUID.randomUUID().toString())
                .message(e.getMessage())
                .status(false)
                .message("Something went wrong!!!")
                .data(e.getMessage())
                .build();
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
