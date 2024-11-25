package org.example.backendkickunity.member.controller;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.example.backendkickunity.auth.AuthService;
import org.example.backendkickunity.member.dto.*;
import org.example.backendkickunity.member.exception.MemberException;
import org.example.backendkickunity.member.service.EmailAuthService;
import org.example.backendkickunity.member.service.MemberService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@Slf4j
@RestController
@RequestMapping("/api/member")
public class MemberApiController {

    private final MemberService memberService;
    private final EmailAuthService emailAuthService;
    private final AuthService authService;

    public MemberApiController(MemberService memberService, EmailAuthService emailAuthService,  AuthService authService) {
        this.memberService = memberService;
        this.emailAuthService = emailAuthService;
        this.authService = authService;
    }

    @PostMapping("/emailSend")
    public ResponseEntity<String> emailSend(@RequestBody EmailCheckRequest request) throws MessagingException, UnsupportedEncodingException {

        // 이메일 유효성 확인
        memberService.emailValidate(request);
        // 이메일 인증을 위한 인증번호 발송
        String authNum = emailAuthService.sendAuthNumber(request.getEmail());

        log.info("이메일 인증번호 발송: {}", authNum);

        return new ResponseEntity<>("이메일 인증번호가 발송되었습니다. 인증을 진행해주세요.", HttpStatus.OK);
    }


    // 이메일 인증번호 확인
    @PostMapping("/emailCheck")
    public ResponseEntity<EmailCheckResponse> emailCheck(@RequestBody EmailCheckRequest request) {

        // 클라이언트에서 받은 이메일과 인증번호를 로그로 출력
        log.info("이메일 인증 요청 - 이메일: {}, 인증번호: {}", request.getEmail(), request.getAuthNum());

        boolean isValid = emailAuthService.validateAuthNumber(request.getEmail(), request.getAuthNum());


        if (isValid) {
            EmailCheckResponse response = new EmailCheckResponse(true, "이메일 인증 성공");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            EmailCheckResponse response = new EmailCheckResponse(false, "인증번호가 잘못되었습니다.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/join")
    public ResponseEntity<Long> addMember(@RequestBody JoinRequest request) {

        Long savedMemberId = memberService.join(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedMemberId);
    }

    @GetMapping("/myPage")
    public ResponseEntity<?> memberInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {

        // Authorization header 에서 이메일 추출
        String email = authService.extractEmailFromAuthorizationHeader(authorizationHeader);

        if (email == null) {
            // 토큰이 유효하지 않거나 이메일을 추출할 수 없는 경우
            return new ResponseEntity<>("사용자가 인증되지 않았습니다.", HttpStatus.UNAUTHORIZED);
        }

        MypageResponse mypageResponse = memberService.myInfoReturn(email);

        // 변환된 MypageResponse를 OK 응답으로 반환
        return new ResponseEntity<>(mypageResponse, HttpStatus.OK);
    }

    // 이름 변경 API
    @PostMapping("/changeName")
    public ResponseEntity<CheckResponse> changeName(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                    @RequestBody ChangeNameRequest request) {

        // Authorization header 에서 이메일 추출
        String email = authService.extractEmailFromAuthorizationHeader(authorizationHeader);

        // MemberService 에서 이름 변경 처리
        CheckResponse response = memberService.changeName(email, request.getNewName());

        // 처리 결과에 따라 적절한 응답 반환
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    // 비밀번호 변경 API
    @PostMapping("/changePassword")
    public ResponseEntity<CheckResponse> changePassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                        @RequestBody ChangePasswordRequest request) {
        // Authorization header 에서 이메일 추출
        String email = authService.extractEmailFromAuthorizationHeader(authorizationHeader);

        try {
            // MemberService 에서 비밀번호 변경 처리
            CheckResponse response = memberService.changePassword(email, request.getOldPassword(), request.getNewPassword());

            // 비밀번호 변경 성공
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (MemberException e) {
            // 예외가 발생하면 전역 예외 처리기로 넘김
            CheckResponse errorResponse = new CheckResponse(false, e.getMessage());
            return new ResponseEntity<>(errorResponse, e.getExceptionType().getHttpStatus());
        }
    }

    // 회원 삭제 API
    @PutMapping("/deleteMember")
    public ResponseEntity<CheckResponse> deleteMember(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                      @RequestBody DeleteMemberRequest request) {
        String email = authService.extractEmailFromAuthorizationHeader(authorizationHeader);
        try {
            // 서비스 레이어에서 회원 삭제
            CheckResponse response = memberService.deleteMember(email, request.getPassword());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (MemberException e) {
            // 전역 예외 처리기로 예외를 처리
            return new ResponseEntity<>(new CheckResponse(false, e.getMessage()), e.getExceptionType().getHttpStatus());
        }
    }
}