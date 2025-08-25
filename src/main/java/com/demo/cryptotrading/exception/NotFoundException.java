package com.demo.cryptotrading.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class NotFoundException extends RuntimeException {

    private HttpStatus code;

    public NotFoundException(HttpStatus code, String message) {
        super(message);
        setCode(code);
    }

}