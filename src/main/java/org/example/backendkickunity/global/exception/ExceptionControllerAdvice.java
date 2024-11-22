// ExceptionControllerAdvice.java
package org.example.backendkickunity.global.exception;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.example.backendkickunity.auth.exception.AuthException;
import org.example.backendkickunity.board.exception.BoardException;
import org.example.backendkickunity.member.exception.MemberException;
import org.example.backendkickunity.team.exception.TeamException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.UnsupportedEncodingException;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {

    // MethodArgumentNotValidException 처리 (검증 예외 처리)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage(), ex);
        ExceptionResponse response = ExceptionResponse.from(ex);  // 검증 오류 메시지를 처리
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);  // 400 Bad Request
    }

    // 이메일 관련 예외 처리
    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleMessagingException(MessagingException ex) {
        log.error("MessagingException 발생: {}", ex.getMessage(), ex);
        ExceptionResponse response = ExceptionResponse.from("이메일 인증 발송에 실패했습니다. 다시 시도해주세요.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);  // 500 Internal Server Error
    }

    // 인코딩 관련 예외 처리
    @ExceptionHandler(UnsupportedEncodingException.class)
    public ResponseEntity<ExceptionResponse> handleUnsupportedEncodingException(UnsupportedEncodingException ex) {
        log.error("UnsupportedEncodingException 발생: {}", ex.getMessage(), ex);
        ExceptionResponse response = ExceptionResponse.from("이메일 인증 발송에 실패했습니다. 다시 시도해주세요.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);  // 500 Internal Server Error
    }

    // 인증 관련 예외 처리
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ExceptionResponse> handleAuthException(AuthException ex) {
        log.error("AuthException 발생: {}", ex.getMessage(), ex);
        ExceptionResponse response = ExceptionResponse.from(ex.getExceptionType());  // AuthException의 메시지 처리
        return new ResponseEntity<>(response, ex.getExceptionType().getHttpStatus());
    }

    // 회원 관련 예외 처리
    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ExceptionResponse> handleMemberException(MemberException ex) {
        log.error("MemberException 발생: {}", ex.getMessage(), ex);
        ExceptionResponse response = ExceptionResponse.from(ex.getExceptionType());  // MemberException의 메시지 처리
        return new ResponseEntity<>(response, ex.getExceptionType().getHttpStatus());
    }

    // 게시판 관련 예외 처리
    @ExceptionHandler(BoardException.class)
    public ResponseEntity<ExceptionResponse> handleBoardException(BoardException ex) {
        log.error("BoardException 발생: {}", ex.getMessage(), ex);
        ExceptionResponse response = ExceptionResponse.from(ex.getExceptionType());  // BoardException의 메시지 처리
        return new ResponseEntity<>(response, ex.getExceptionType().getHttpStatus());
    }

    // 팀 관련 예외 처리
    @ExceptionHandler(TeamException.class)
    public ResponseEntity<ExceptionResponse> handleTeamException(TeamException ex) {
        log.error("TeamException 발생: {}", ex.getMessage(), ex);
        ExceptionResponse response = ExceptionResponse.from(ex.getExceptionType());  // TeamException의 메시지 처리
        return new ResponseEntity<>(response, ex.getExceptionType().getHttpStatus());
    }

    // 모든 예외 처리 (Generic)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGenericException(Exception ex) {
        log.error("예외 발생: {}", ex.getMessage(), ex);
        ExceptionResponse response = ExceptionResponse.from("처리 중 오류가 발생했습니다. 다시 시도해주세요.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);  // 500 Internal Server Error
    }
}
