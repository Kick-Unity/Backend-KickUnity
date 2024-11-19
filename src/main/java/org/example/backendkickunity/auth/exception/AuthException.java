package org.example.backendkickunity.auth.exception;

import org.example.backendkickunity.global.exception.BaseException;
import org.example.backendkickunity.global.exception.ExceptionResponse;

public class AuthException extends BaseException {

    private final AuthExceptionType exceptionType;

    public AuthException(AuthExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public AuthExceptionType getExceptionType() {
        return this.exceptionType;
    }

    // ExceptionResponse 반환하도록 수정
    public ExceptionResponse toExceptionResponse() {
        return ExceptionResponse.from(this.exceptionType);  // exceptionType 에서 메시지와 코드 반환
    }
}
