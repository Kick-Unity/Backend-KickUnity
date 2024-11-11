package org.example.backendkickunity.global.exception;

import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.UnsupportedEncodingException;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<String> handleMessagingException(MessagingException ex) {
        // 메시지 관련 예외 처리
        return new ResponseEntity<>("이메일 인증 발송에 실패했습니다. 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnsupportedEncodingException.class)
    public ResponseEntity<String> handleUnsupportedEncodingException(UnsupportedEncodingException ex) {
        // 인코딩 관련 예외 처리
        return new ResponseEntity<>("이메일 인증 발송에 실패했습니다. 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        // 기타 일반적인 예외 처리
        return new ResponseEntity<>("처리 중 오류가 발생했습니다. 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

