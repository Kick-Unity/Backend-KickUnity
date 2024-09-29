package org.example.backendkickunity.exception;

import org.springframework.http.HttpStatus;

public enum MemberExceptionType implements BaseExceptionType {

    /*
    * 회원가입
    * */
    ALREADY_EXIST_MEMBER_ID(HttpStatus.CONFLICT,"JOIN_001", "이미 가입된 아이디입니다."),
    ALREADY_EXIST_EMAIL(HttpStatus.CONFLICT,"JOIN_002", "이미 가입된 이메일입니다."),
    INVALID_MEMBER_ID_FORMAT(HttpStatus.CONFLICT,"JOIN_003", "잘못된 형식의 아이디입니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.CONFLICT,"JOIN_004", "잘못된 형식의 비밀번호입니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.CONFLICT,"JOIN_005", "잘못된 형식의 이메일입니다."),

    MEMBER_NOT_EXIST(HttpStatus.CONFLICT, "MEMBER_001", "멤버가 존재하지 않습니다."),
    MEMBER_DELETED(HttpStatus.CONFLICT, "MEMBER_002", "탈퇴한 멤버입니다."),
    MEMBER_INVALID_ID_AND_PASSWORD(HttpStatus.CONFLICT, "MEMBER_003", "아이디나 비밀번호가 다릅니다."),
    MEMBER_WRONG_PASSWORD(HttpStatus.CONFLICT, "MEMBER_004", "비밀번호가 일치하지 않습니다.");


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
