package com.buhoristeca.lector.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PrestamoValidationException extends RuntimeException {
    public PrestamoValidationException(String message) {
        super(message);
    }
}