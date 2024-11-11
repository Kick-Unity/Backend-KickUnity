package org.example.backendkickunity.member.controller;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.example.backendkickunity.member.dto.EmailCheckRequest;
import org.example.backendkickunity.member.dto.JoinRequest;
import org.example.backendkickunity.member.service.MailService;
import org.example.backendkickunity.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@Slf4j
@RestController
public class MemberApiController {

    private final MemberService memberService;
    private final MailService mailService;

    public MemberApiController(MemberService memberService, MailService mailService) {
        this.memberService = memberService;
        this.mailService = mailService;
    }

    @PostMapping("/api/join")
    public ResponseEntity<Long> addMember(@RequestBody JoinRequest request) throws MessagingException, UnsupportedEncodingException {

        Long savedMemberId = memberService.join(request);

        // 회원 정보 저장 후 이메일 인증을 위한 인증번호 발송
        String authNum = mailService.sendMail(request.getEmail());

        log.info("회원가입 후 이메일 인증번호 발송: {}", authNum);

//        return new ResponseEntity<>("이메일 인증번호가 발송되었습니다. 인증을 진행해주세요.", HttpStatus.OK);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedMemberId);
    }

    // 이메일 인증번호 확인
    @PostMapping("/api/join/emailCheck")
    public ResponseEntity<String> emailCheck(@RequestBody EmailCheckRequest request) {
        boolean isValid = mailService.checkAuthNumber(request.getAuthNum());

        if (isValid) {
            // 인증번호가 맞으면 인증 완료 처리 (예: 인증 완료 상태로 변경)
            return new ResponseEntity<>("이메일 인증 성공", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("인증번호가 잘못되었습니다.", HttpStatus.BAD_REQUEST);
        }
    }

}