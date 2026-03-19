package com.avbinvest.user.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND) // Error 404
    public ErrorResponse handlerCompanyNotFoundException(CompanyNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT) // Error 409
    public ErrorResponse handlerDuplicateUserException(DuplicateUserException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND) // Error 404
    public ErrorResponse handlerUserNotFoundException(UserNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }
}
