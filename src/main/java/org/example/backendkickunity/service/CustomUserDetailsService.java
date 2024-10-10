package org.example.backendkickunity.service;

import org.example.backendkickunity.domain.member.Member;
import org.example.backendkickunity.dto.CustomMemberDetails;
import org.example.backendkickunity.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomMemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomMemberDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //DB에서 조회
        Member memberData = memberRepository.findByEmail(email);

        if (memberData != null) {
            //MemberDetails에 담아 리턴하면 AuthenticationManager가 검증
            return new CustomMemberDetails(memberData);
        }

        return null;
    }
}
