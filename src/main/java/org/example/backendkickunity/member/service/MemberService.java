package org.example.backendkickunity.member.service;

import org.example.backendkickunity.member.dto.JoinRequest;
import org.example.backendkickunity.member.domain.Member;
import org.example.backendkickunity.member.exception.MemberException;
import org.example.backendkickunity.member.exception.MemberExceptionType;
import org.example.backendkickunity.member.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    //회원가입
    @Transactional
    public Long join(JoinRequest joinRequest) {
        String email = joinRequest.getEmail();
        String password = joinRequest.getPassword();
        String name = joinRequest.getName();
        String birth = joinRequest.getBirth();

        // 이메일 중복 체크
        isExistEmail(email);

        // 이메일, 비밀번호 형식 체크
        checkEmailValid(email);
        checkPasswordValid(password);

        // 이름 중복 체크
        isExistName(name);

        // 8자리 생년월일 체크
        checkBirthValid(birth);

        Member member = new Member();
        member.setEmail(email);
        member.setPassword(bCryptPasswordEncoder.encode(password));
        member.setName(name);
        member.setBirth(birth);
        member.setRole("ROLE_USER");

        memberRepository.save(member);

        return member.getId();
    }

    private void isExistEmail(String email) {
        if(memberRepository.existsByEmail(email)){
            throw new MemberException(MemberExceptionType.ALREADY_EXIST_EMAIL);
        }
    }

    private void checkEmailValid(String email) {
        // 이메일 유효성 검사 정규표현식
        String EMAIL_FORMAT = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        if(email == null || !email.matches(EMAIL_FORMAT)){
            throw new MemberException(MemberExceptionType.INVALID_EMAIL_FORMAT);
        }
    }

    private void checkPasswordValid(String password) {
        // 사용자 비밀번호는 영문, 숫자, 하나 이상의 특수문자를 포함하는 8 ~ 16자
        String PASSWORD_FORMAT = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$";

        if(password == null ||!password.matches(PASSWORD_FORMAT)){
            throw new MemberException(MemberExceptionType.INVALID_PASSWORD_FORMAT);
        }
    }

    private void isExistName(String name) {
        if(memberRepository.existsByName(name)){
            throw new MemberException(MemberExceptionType.ALREADY_EXIST_NAME);
        }
    }

    private void checkBirthValid(String birth) {
        String BIRTH_FORMAT = "^(19[0-9][0-9]|20\\d{2})(0[0-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])$";

        if(birth == null || !birth.matches(BIRTH_FORMAT)){
            throw new MemberException(MemberExceptionType.INVALID_BIRTH_FORMAT);
        }
    }

}
