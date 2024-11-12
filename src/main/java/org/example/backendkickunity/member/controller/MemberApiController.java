package org.example.backendkickunity.member.controller;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.example.backendkickunity.auth.util.MemberAuthorizationUtil;
import org.example.backendkickunity.member.domain.Member;
import org.example.backendkickunity.member.dto.EmailCheckRequest;
import org.example.backendkickunity.member.dto.JoinRequest;
import org.example.backendkickunity.member.dto.MemberDTO;
import org.example.backendkickunity.member.repository.MemberRepository;
import org.example.backendkickunity.member.service.MailService;
import org.example.backendkickunity.member.service.MemberService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@Slf4j
@RestController
@RequestMapping("/api/member")
public class MemberApiController {

    private final MemberService memberService;
    private final MailService mailService;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper; // ModelMapper 주입


    public MemberApiController(MemberService memberService, MailService mailService, MemberRepository memberRepository, ModelMapper modelMapper) {
        this.memberService = memberService;
        this.mailService = mailService;
        this.memberRepository = memberRepository;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/emailSend")
    public ResponseEntity<String> emailSend(@RequestBody EmailCheckRequest request) throws MessagingException, UnsupportedEncodingException {

        // 이메일 유효성 확인
        memberService.emailValidate(request);
        // 이메일 인증을 위한 인증번호 발송
        String authNum = mailService.sendMail(request.getEmail());

        log.info("회원가입 후 이메일 인증번호 발송: {}", authNum);

        return new ResponseEntity<>("이메일 인증번호가 발송되었습니다. 인증을 진행해주세요.", HttpStatus.OK);
    }


    // 이메일 인증번호 확인
    @PostMapping("/emailCheck")
    public ResponseEntity<String> emailCheck(@RequestBody EmailCheckRequest request) {
        boolean isValid = mailService.checkAuthNumber(request.getAuthNum());

        if (isValid) {
            // 인증번호가 맞으면 인증 완료 처리 (예: 인증 완료 상태로 변경)
            return new ResponseEntity<>("이메일 인증 성공", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("인증번호가 잘못되었습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/join")
    public ResponseEntity<Long> addMember(@RequestBody JoinRequest request) throws MessagingException, UnsupportedEncodingException {

        Long savedMemberId = memberService.join(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedMemberId);
    }

    @GetMapping("/memberInfo")
    public ResponseEntity<?> memberInfo() {
        String email = MemberAuthorizationUtil.getLoginMemberEmail();  // 이메일 가져오기

        if (email == null) {
            return new ResponseEntity<>("사용자가 인증되지 않았습니다.", HttpStatus.UNAUTHORIZED);
        }

        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            return new ResponseEntity<>("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        // ModelMapper를 사용하여 Member를 MemberDTO로 변환
        MemberDTO memberDTO = modelMapper.map(member, MemberDTO.class);

        return new ResponseEntity<>(memberDTO, HttpStatus.OK);
    }



}