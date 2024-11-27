package org.example.backendkickunity.chat.exception;

import org.example.backendkickunity.global.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum ChatExceptionType implements BaseExceptionType {
    ALREADY_EXIST_NAME(HttpStatus.BAD_REQUEST, "CHAT_001", "이미 가입된 이메일입니다.");

    private HttpStatus httpStatus;
    private String errorCode;
    private String errorMessage;


    ChatExceptionType(HttpStatus httpStatus, String errorCode, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }

}

