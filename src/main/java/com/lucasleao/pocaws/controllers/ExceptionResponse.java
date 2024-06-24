package com.lucasleao.pocaws.controllers;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@Builder
public class ExceptionResponse {

    private String message;
    private String details;
    private HttpStatus code;

}
