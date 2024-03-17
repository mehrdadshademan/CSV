package com.b2b.lib.controller;

import com.b2b.lib.dto.ExceptionDto;
import com.b2b.lib.exception.InputDataException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionLibHandler {

    @ExceptionHandler({InputDataException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ExceptionDto processAbstractException(InputDataException ex) {
        return new ExceptionDto(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
