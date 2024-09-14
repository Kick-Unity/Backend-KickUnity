package org.example.backedkickunity.exception;

import org.springframework.http.HttpStatus;

public interface BaseExceptionType {

    HttpStatus httpStatus();
    String errorMessage();
}
