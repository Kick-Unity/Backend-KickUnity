package org.example.backendkickunity.board.exception;

import org.example.backendkickunity.global.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum BoardExceptionType implements BaseExceptionType {

    /*
     *
     * */
    BOARD_NOT_EXIST(HttpStatus.NOT_FOUND, "BOARD_001", "게시글이 존재하지 않습니다."),
    BOARD_UNAUTHORIZED_UPDATE(HttpStatus.FORBIDDEN, "BOARD_002", "게시글 수정 권한이 없습니다."),
    BOARD_UNAUTHORIZED_DELETE(HttpStatus.FORBIDDEN, "BOARD_003", "게시글 삭제 권한이 없습니다.");

    private HttpStatus httpStatus;
    private String errorCode;
    private String errorMessage;

    BoardExceptionType(HttpStatus httpStatus, String errorCode, String errorMessage) {
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
