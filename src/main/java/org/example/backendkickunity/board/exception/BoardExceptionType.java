package org.example.backendkickunity.board.exception;

import org.example.backendkickunity.global.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum BoardExceptionType implements BaseExceptionType {

    /*
     *
     * */
    BOARD_NOT_EXIST(HttpStatus.NOT_FOUND, "BOARD_001", "게시글이 존재하지 않습니다."), // 게시글이 없을 때는 404
    BOARD_UNAUTHORIZED_UPDATE(HttpStatus.FORBIDDEN, "BOARD_002", "게시글 수정 권한이 없습니다."), // 수정 권한이 없을 때는 403
    BOARD_UNAUTHORIZED_DELETE(HttpStatus.FORBIDDEN, "BOARD_003", "게시글 삭제 권한이 없습니다."), // 삭제 권한이 없을 때는 403
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "BOARD_004", "게시판 정보가 유효하지 않습니다."); // 잘못된 카테고리 값은 400

    private final  HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;

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
