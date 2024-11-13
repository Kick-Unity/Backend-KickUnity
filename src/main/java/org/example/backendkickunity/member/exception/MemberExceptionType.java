package org.example.backendkickunity.member.exception;

import org.example.backendkickunity.global.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum MemberExceptionType implements BaseExceptionType {

    /*
     * 회원가입 관련
     * */
    ALREADY_EXIST_EMAIL(HttpStatus.BAD_REQUEST, "JOIN_001", "이미 가입된 이메일입니다."),
    ALREADY_EXIST_NAME(HttpStatus.BAD_REQUEST, "JOIN_002", "존재하는 닉네임 입니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "JOIN_003", "잘못된 형식의 이메일입니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "JOIN_004", "잘못된 형식의 비밀번호입니다."),
    INVALID_BIRTH_FORMAT(HttpStatus.BAD_REQUEST, "JOIN_005", "잘못된 형식의 생년월일 입니다."),
    WRONG_EMAIL_AUTHCODE(HttpStatus.BAD_REQUEST, "JOIN_006", "이메일 인증 번호가 일치하지 않습니다."),

    /*
     * 멤버 관련
     * */
    MEMBER_NOT_EXIST(HttpStatus.NOT_FOUND, "MEMBER_001", "멤버가 존재하지 않습니다."),
    MEMBER_INVALID_ID_AND_PASSWORD(HttpStatus.UNAUTHORIZED, "MEMBER_002", "아이디나 비밀번호가 다릅니다."),
    MEMBER_WRONG_PASSWORD(HttpStatus.UNAUTHORIZED, "MEMBER_003", "비밀번호가 일치하지 않습니다."),
    MEMBER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "MEMBER_004", "사용자가 인증되지 않았습니다.");

    private HttpStatus httpStatus;
    private String errorCode;
    private String errorMessage;

    MemberExceptionType(HttpStatus httpStatus, String errorCode, String errorMessage) {
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
