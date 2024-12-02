package org.example.backendkickunity.chat.exception;

import org.example.backendkickunity.global.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum ChatExceptionType implements BaseExceptionType {
    CHATROOM_NOT_EXIST(HttpStatus.NOT_FOUND, "CHAT_001", "채팅방이 존재하지 않습니다."),
    INVALID_SENDER(HttpStatus.BAD_REQUEST, "CHAT_002", "발신자 정보가 유효하지 않습니다."),
    NOT_MEMBER_OF_CHATROOM(HttpStatus.BAD_REQUEST, "CHAT_003", "채팅 참여자가 아닙니다.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;


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