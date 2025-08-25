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
public class CryptoTradingException extends RuntimeException {

    private HttpStatus code;

    public CryptoTradingException(HttpStatus code, String message) {
        super(message);
        setCode(code);
    }

}