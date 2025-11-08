package org.example.backendkickunity.team.exception;

import org.example.backendkickunity.global.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum TeamExceptionType implements BaseExceptionType {
    /*
     * 팀 생성 관련
     * */
    ALREADY_EXIST_NAME(HttpStatus.BAD_REQUEST, "TEAM_001", "이미 존재하는 팀 이름입니다."),
    ALREADY_HAVE_TEAM(HttpStatus.BAD_REQUEST, "TEAM_002", "이미 팀 정보가 존재하는 회원입니다."),
    TEAM_NOT_EXIST(HttpStatus.BAD_REQUEST, "TEAM_003", "팀 정보가 존재하지 않습니다."),
    UNAUTHORIZED_TEAM_LEADER(HttpStatus.BAD_REQUEST, "TEAM_004", "해당 팀의 팀장 권한이 없습니다."),
    NOT_YOUR_MEMBER(HttpStatus.FORBIDDEN, "TEAM_005", "해당 팀의 멤버가 아닙니다.");


    private HttpStatus httpStatus;
    private String errorCode;
    private String errorMessage;


    TeamExceptionType(HttpStatus httpStatus, String errorCode, String errorMessage) {
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





