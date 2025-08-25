package com.demo.cryptotrading.exception;

import com.demo.cryptotrading.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> generalException(Exception ex) {
        return buildErrorResponse("Internal server error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CryptoTradingException.class)
    public ResponseEntity<ErrorResponse> cryptoTradingException(CryptoTradingException ex) {
        HttpStatus statusCode = ex.getCode() != null ? ex.getCode() : HttpStatus.INTERNAL_SERVER_ERROR;
        return buildErrorResponse(ex.getMessage(), statusCode);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> badRequestException(BadRequestException ex) {
        HttpStatus statusCode = ex.getCode() != null ? ex.getCode() : HttpStatus.BAD_REQUEST;
        return buildErrorResponse(ex.getMessage(), statusCode);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> notFoundException(NotFoundException ex) {
        HttpStatus statusCode = ex.getCode() != null ? ex.getCode() : HttpStatus.NOT_FOUND;
        return buildErrorResponse(ex.getMessage(), statusCode);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> insufficientBalanceException(InsufficientBalanceException ex) {
        HttpStatus statusCode = ex.getCode() != null ? ex.getCode() : HttpStatus.INTERNAL_SERVER_ERROR;
        return buildErrorResponse(ex.getMessage(), statusCode);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status) {
        return new ResponseEntity<>(new ErrorResponse(message), status);
    }
}
