package org.example.backendkickunity.exception;

import org.springframework.http.HttpStatus;

public interface BaseExceptionType {
    HttpStatus getHttpStatus();
    String getErrorCode();
    String getErrorMessage();
}




