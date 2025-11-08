package org.example.backendkickunity.global.exception;

import org.springframework.http.HttpStatus;

public interface BaseExceptionType {
    HttpStatus getHttpStatus();  // HTTP 상태 코드
    String getErrorCode();  // 에러 코드
    String getErrorMessage();  // 에러 메시지
}
