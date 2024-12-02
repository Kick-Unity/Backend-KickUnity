package org.example.backendkickunity.member;

import org.example.backendkickunity.member.domain.Member;
import org.example.backendkickunity.member.repository.MemberRepository;
import org.example.backendkickunity.team.domain.Team;
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

    @Test
    public void testMemberCreation() {
        // Team 객체가 null일 수도 있다면, Team 객체를 null로 설정
        Team team = null;

        // Member 객체 생성
        Member member = Member.builder()
                .email("test1@example.com") // 유니크한 이메일
                .password("password123!!")    // 필수값
                .name("John Doe")           // 필수값
                .birth("19900101")        // 필수값
                .role("USER")               // 선택적 필드
                .team(null)                 // team은 null일 수 있음
                .build();

        memberRepository.save(member); // MemberRepository로 저장
    }

}
