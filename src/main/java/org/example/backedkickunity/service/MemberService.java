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
        String password = passwordEncoder.encode(joinRequest.getPassword());
        String email = joinRequest.getEmail();

        // 아이디, 이메일 중복 체크
        checkMemberIdUnique(memberId);
        checkEmailUnique(email);

        Member member = Member.builder()
                .memberId(joinRequest.getMemberId())
                .name(joinRequest.getName())
                .password(passwordEncoder.encode(joinRequest.getPassword()))
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



}
