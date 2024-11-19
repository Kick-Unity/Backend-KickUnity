package org.example.backendkickunity.member.service;

import org.example.backendkickunity.member.domain.MemberRole;
import org.example.backendkickunity.member.dto.CheckResponse;
import org.example.backendkickunity.member.dto.EmailCheckRequest;
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

    //이메일 체크
    public void emailValidate(EmailCheckRequest emailCheckRequest) throws MemberException {
        String email = emailCheckRequest.getEmail();

        // 이메일 중복 체크
        isExistEmail(email);

        // 이메일 형식 체크
        checkEmailValid(email);
    }

    //회원가입
    @Transactional
    public Long join(JoinRequest joinRequest) {
        String email = joinRequest.getEmail();
        String password = joinRequest.getPassword();
        String name = joinRequest.getName();
        String birth = joinRequest.getBirth();

        // 비밀번호 형식 체크
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
        member.setRole(MemberRole.valueOf("ROLE_USER"));

        memberRepository.save(member);

        return member.getId();
    }

    //이름 변경
    public CheckResponse changeName(String email, String newName) throws MemberException {

        //이메일로 회원 찾기
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new MemberException(MemberExceptionType.MEMBER_NOT_EXIST);
        }

        //닉네임 중복 확인
        isExistName(newName);

        //새로운 이름으로 변경
        member.setName(newName);
        memberRepository.save(member); // 변경된 이름을 DB에 저장

        return new CheckResponse(true, "이름이 성공적으로 변경되었습니다.");
    }

    //비밀번호 변경
    public CheckResponse changePassword(String email, String oldPassword, String newPassword) throws MemberException {

        //이메일로 회원 찾기
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new MemberException(MemberExceptionType.MEMBER_NOT_EXIST);
        }

        // 기존 비밀번호 확인
        if (!bCryptPasswordEncoder.matches(oldPassword, member.getPassword())) {
            throw new MemberException(MemberExceptionType.MEMBER_WRONG_PASSWORD); // 기존 비밀번호가 틀린 경우
        }

        //변경 비밀번호 형식 체크
        checkPasswordValid(newPassword);

        // 새로운 비밀번호로 변경 (암호화하여 저장)
        member.setPassword(bCryptPasswordEncoder.encode(newPassword));

        // 변경된 비밀번호를 DB에 저장
        memberRepository.save(member);

        // 성공 메시지 반환
        return new CheckResponse(true, "비밀번호가 성공적으로 변경되었습니다.");

    }

    //회원 삭제
    @Transactional
    public CheckResponse deleteMember(String email, String password) throws MemberException {

        // 이메일로 회원 찾기
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new MemberException(MemberExceptionType.MEMBER_NOT_EXIST); // 회원이 존재하지 않으면 예외
        }

        // 비밀번호 확인
        if (!bCryptPasswordEncoder.matches(password, member.getPassword())) {
            throw new MemberException(MemberExceptionType.MEMBER_WRONG_PASSWORD); // 비밀번호가 맞지 않으면 예외
        }

        // 회원 삭제
        memberRepository.delete(member);

        // 성공적으로 삭제되었음을 알리는 메시지 반환
        return new CheckResponse(true, "회원이 성공적으로 삭제되었습니다.");
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

    public Member findByMemberEmail(String email) {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new MemberException(MemberExceptionType.MEMBER_NOT_EXIST);
        }

        return member;
    }
}
