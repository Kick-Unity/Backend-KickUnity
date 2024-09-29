package org.example.backendkickunity.member;

import org.example.backendkickunity.domain.member.Member;
import org.example.backendkickunity.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MemberRepositoryTest {

    //MemberRepository를 사용하기 위해 빈 객체를 끌어옴
    @Autowired private MemberRepository memberRepository;

    @Test
    public void crudTest() {
        Member member = Member.builder()
                .email("qqqqq")
                .name("냐미")
                .password("1234")
                .build();

        //create test
        memberRepository.save(member);

        //get test
        Member foundMember = memberRepository.findById(1L).get();
    }

}
