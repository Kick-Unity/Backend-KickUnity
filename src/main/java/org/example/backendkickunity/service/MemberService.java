package org.example.backendkickunity.service;

import lombok.RequiredArgsConstructor;
import org.example.backendkickunity.controller.dto.JoinRequest;
import org.example.backendkickunity.controller.dto.LoginRequest;
import org.example.backendkickunity.domain.member.Member;
import org.example.backendkickunity.exception.MemberException;
import org.example.backendkickunity.exception.MemberExceptionType;
import org.example.backendkickunity.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long join(JoinRequest joinRequest) {
        String email = joinRequest.getEmail();
        String password = joinRequest.getPassword();
        String name = joinRequest.getName();

        // 이메일, 비밀번호 형식 체크
        checkEmailValid(email);
        checkPasswordValid(password);

        // 이메일 중복 체크
        checkEmailUnique(email);

        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .build();
        memberRepository.save(member);

        return member.getId();
    }

    // 로그인 메서드 추가
    public Long login(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        // 사용자가 존재하는지 확인
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_EXIST));

        // 비밀번호 검증
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new MemberException(MemberExceptionType.MEMBER_WRONG_PASSWORD);
        }

        return member.getId(); // 로그인 성공 시 사용자 정보를 반환
    }

    private void checkEmailUnique(String email) {
        if(memberRepository.existsByEmail(email)){
            throw new MemberException(MemberExceptionType.ALREADY_EXIST_EMAIL);
        }
    }

    private void checkEmailValid(String email) {
        // 이메일 유효성 검사 정규표현식
        String EMAIL_FORMAT = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        if(!email.matches(EMAIL_FORMAT)){
            throw new MemberException(MemberExceptionType.INVALID_EMAIL_FORMAT);
        }
    }

    private void checkPasswordValid(String password) {
        // 사용자 비밀번호는 영문, 숫자, 하나 이상의 특수문자를 포함하는 8 ~ 16자
        String PASSWORD_FORMAT = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$";

        if(!password.matches(PASSWORD_FORMAT)){
            throw new MemberException(MemberExceptionType.INVALID_PASSWORD_FORMAT);
        }
    }

}
