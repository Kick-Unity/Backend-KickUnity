package org.example.backendkickunity.service;

import lombok.RequiredArgsConstructor;
import org.example.backendkickunity.domain.member.Member;
import org.example.backendkickunity.exception.MemberException;
import org.example.backendkickunity.exception.MemberExceptionType;
import org.example.backendkickunity.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberFindService {

    private final MemberRepository memberRepository;

    public Member findMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_EXIST));

        validateMember(member);

        return member;
    }

    public Member findByMemberIdAndPassword(String memberId, String password) {
        Member member = memberRepository.findByMemberIdAndPassword(memberId, password)
                .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_INVALID_ID_AND_PASSWORD));

        validateMember(member);
        return member;
    }

    private void validateMember(Member member) {
        if (member.isDeleted()) {
            throw new MemberException(MemberExceptionType.MEMBER_DELETED);
        }
    }
}
