package org.example.backedkickunity.service;

import lombok.RequiredArgsConstructor;
import org.example.backedkickunity.controller.dto.JoinRequest;
import org.example.backedkickunity.domain.Member;
import org.example.backedkickunity.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public String join(JoinRequest joinRequest) {
        Member member = Member.builder()
                .memberId(joinRequest.getMemberId())
                .name(joinRequest.getName())
                .password(joinRequest.getPassword())
                .phone(joinRequest.getPhone())
                .build();
        memberRepository.save(member);

        return member.getMemberId();
    }

}
