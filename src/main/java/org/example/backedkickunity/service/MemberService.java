package org.example.backedkickunity.service;

import lombok.RequiredArgsConstructor;
import org.example.backedkickunity.controller.dto.JoinRequest;
import org.example.backedkickunity.domain.member.Member;
import org.example.backedkickunity.domain.member.exception.MemberException;
import org.example.backedkickunity.domain.member.exception.MemberExceptionType;
import org.example.backedkickunity.repository.MemberRepository;
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
        String memberId = joinRequest.getMemberId();
        String password = joinRequest.getPassword();
        String email = joinRequest.getEmail();
        String name = joinRequest.getName();

        // 아이디, 비밀번호, 이메일 형식 체크
        checkMemberIdValid(memberId);
        checkPasswordValid(password);
        checkEmailValid(email);

        // 아이디, 이메일 중복 체크
        checkMemberIdUnique(memberId);
        checkEmailUnique(email);


        Member member = Member.builder()
                .memberId(memberId)
                .password(passwordEncoder.encode(password))
                .email(email)
                .name(name)
                .build();

        memberRepository.save(member);

        return member.getId();
    }

    private void checkMemberIdUnique(String memberId) {
        if(memberRepository.existsByMemberId(memberId)){
            throw new MemberException(MemberExceptionType.ALREADY_EXIST_MEMBER_ID);
        }
    }

    private void checkEmailUnique(String memberId) {
        if(memberRepository.existsByMemberId(memberId)){
            throw new MemberException(MemberExceptionType.ALREADY_EXIST_EMAIL);
        }
    }

    private void checkMemberIdValid(String memberId) {
        // 사용자 아이디는영문, 숫자 조합의 6 ~ 10자
        String MEMBER_ID_FORMAT = "^[a-z]{1}[a-z0-9]{5,10}$";

        if(!memberId.matches(MEMBER_ID_FORMAT)){
            throw new MemberException(MemberExceptionType.INVALID_MEMBER_ID_FORMAT);
        }
    }

    private void checkPasswordValid(String password) {
        // 사용자 비밀번호는 영문, 숫자, 하나 이상의 특수문자를 포함하는 8 ~ 16자
        String PASSWORD_FORMAT = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$";

        if(!password.matches(PASSWORD_FORMAT)){
            throw new MemberException(MemberExceptionType.INVALID_PASSWORD_FORMAT);
        }
    }

    private void checkEmailValid(String email) {
        // 이메일 유효성 검사 정규표현식
        String EMAIL_FORMAT = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        if(!email.matches(EMAIL_FORMAT)){
            throw new MemberException(MemberExceptionType.INVALID_EMAIL_FORMAT);
        }
    }



}
