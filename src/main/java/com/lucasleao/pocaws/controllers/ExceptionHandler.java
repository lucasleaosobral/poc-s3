package com.lucasleao.pocaws.controllers;


import com.lucasleao.pocaws.core.domain.exceptions.FileException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(FileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleFileException(FileException exception) {
        return ExceptionResponse.builder()
                .message("Error while dealing with file.")
                .details(exception.getMessage())
                .code(HttpStatus.BAD_REQUEST)
                .build();
    }
}
