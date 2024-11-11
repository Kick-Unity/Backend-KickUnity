package org.example.backendkickunity.member.service;

import org.example.backendkickunity.member.domain.Member;
import org.example.backendkickunity.member.dto.CustomUserDetails;
import org.example.backendkickunity.member.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //DB에서 조회
        Member member = memberRepository.findByEmail(email);

        if (member != null) {
            //MemberDetails에 담아 리턴하면 AuthenticationManager가 검증
            return new CustomUserDetails(member);
        }

        return null;
    }
}
