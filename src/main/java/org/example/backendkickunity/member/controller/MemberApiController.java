package org.example.backendkickunity.member.controller;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.example.backendkickunity.auth.util.MemberAuthorizationUtil;
import org.example.backendkickunity.member.domain.Member;
import org.example.backendkickunity.member.dto.EmailCheckRequest;
import org.example.backendkickunity.member.dto.EmailCheckResponse;
import org.example.backendkickunity.member.dto.JoinRequest;
import org.example.backendkickunity.member.dto.MypageResponse;
import org.example.backendkickunity.member.repository.MemberRepository;
import org.example.backendkickunity.member.service.EmailAuthService;
import org.example.backendkickunity.member.service.MemberService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@Slf4j
@RestController
public class MemberApiController {

    private final MemberService memberService;
    private final EmailAuthService emailAuthService;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper; // ModelMapper 주입


    public MemberApiController(MemberService memberService, EmailAuthService emailAuthService, MemberRepository memberRepository, ModelMapper modelMapper) {
        this.memberService = memberService;
        this.emailAuthService = emailAuthService;
        this.memberRepository = memberRepository;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/api/member/emailSend")
    public ResponseEntity<String> emailSend(@RequestBody EmailCheckRequest request) throws MessagingException, UnsupportedEncodingException {

        // 이메일 유효성 확인
        memberService.emailValidate(request);
        // 이메일 인증을 위한 인증번호 발송
        String authNum = emailAuthService.sendAuthNumber(request.getEmail());

        log.info("이메일 인증번호 발송: {}", authNum);

        return new ResponseEntity<>("이메일 인증번호가 발송되었습니다. 인증을 진행해주세요.", HttpStatus.OK);
    }


    // 이메일 인증번호 확인
    @PostMapping("/api/member/emailCheck")
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

    @PostMapping("/api/member/join")
    public ResponseEntity<Long> addMember(@RequestBody JoinRequest request) throws MessagingException, UnsupportedEncodingException {

        Long savedMemberId = memberService.join(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedMemberId);
    }

    @GetMapping("/api/member/myPage")
    public ResponseEntity<?> memberInfo() {
        // 로그인된 사용자의 이메일을 가져오기
        String email = MemberAuthorizationUtil.getLoginMemberEmail();

        if (email == null) {
            // 사용자가 인증되지 않았으면 UNAUTHORIZED 응답 반환
            return new ResponseEntity<>("사용자가 인증되지 않았습니다.", HttpStatus.UNAUTHORIZED);
        }

        // 해당 이메일로 회원을 찾기
        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            // 회원이 존재하지 않으면 NOT_FOUND 응답 반환
            return new ResponseEntity<>("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        // ModelMapper를 사용하여 Member 엔티티를 MypageResponse DTO로 변환
        MypageResponse mypageResponse = modelMapper.map(member, MypageResponse.class);

        // 변환된 MypageResponse를 OK 응답으로 반환
        return new ResponseEntity<>(mypageResponse, HttpStatus.OK);
    }




}