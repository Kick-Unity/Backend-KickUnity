package org.example.backendkickunity.team.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backendkickunity.member.domain.Member;
import org.example.backendkickunity.member.exception.MemberException;
import org.example.backendkickunity.member.exception.MemberExceptionType;
import org.example.backendkickunity.member.repository.MemberRepository;
import org.example.backendkickunity.team.domain.Team;
import org.example.backendkickunity.team.dto.AddTeamRequest;
import org.example.backendkickunity.team.dto.UpdateTeamRequest;
import org.example.backendkickunity.team.exception.TeamException;
import org.example.backendkickunity.team.exception.TeamExceptionType;
import org.example.backendkickunity.team.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository; // Team 관련 데이터 접근
    private final MemberRepository memberRepository; // Member 관련 데이터 접근

    // 팀 생성
    @Transactional
    public Long createTeam(String leaderEmail, AddTeamRequest request) {

        // 팀장 조회
        Member leader = memberRepository.findByEmail(leaderEmail);
        if (leader == null) {
            throw new MemberException(MemberExceptionType.MEMBER_NOT_EXIST);
        } else if (leader.getTeam() != null) {
            throw new TeamException(TeamExceptionType.ALREADY_HAVE_TEAM);
        }

        // 팀 이름 중복 검사
        if (teamRepository.existsByTeamName(request.getTeamName())) {
            throw new TeamException(TeamExceptionType.ALREADY_EXIST_NAME);
        }

        Team team = new Team();
        team.setTeamName(request.getTeamName());
        team.setTeamCategory(request.getTeamCategory());
        team.setTeamStartDate(request.getTeamStartDate());
        team.setTeamRegion(request.getTeamRegion());
        team.setTeamAge(request.getTeamAge());
        team.setTeamSize(request.getTeamSize());
        team.setTeamDescription(request.getTeamDescription());

        // 팀장을 팀에 추가
        team.setTeamLeader(leader);
        team.getMembers().add(leader);  // 팀의 첫 번째 멤버로 팀장 추가
        // 팀장 정보 수정
        leader.setTeam(team); // 팀장의 팀 정보 삽입
        leader.setRole("ROLE_ADMIN"); // 팀장 역할 부여

        memberRepository.save(leader); // 팀장 정보 업데이트
        teamRepository.save(team); // 팀 정보 저장

        log.info("새로운 팀이 생성되었습니다. 팀 이름: {}, 팀장: {}", request.getTeamName(), leader.getName());
        return team.getId();
    }

    // 팀 정보 수정
    @Transactional
    public void updateTeam(String leaderEmail, Long teamId, UpdateTeamRequest request) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(TeamExceptionType.TEAM_NOT_EXIST));

        // 팀장 조회
        Member leader = memberRepository.findByEmail(leaderEmail);
        if (leader == null) {
            throw new MemberException(MemberExceptionType.MEMBER_NOT_EXIST);
        } else if (!team.getTeamLeader().getId().equals(leader.getId())) {
            throw new TeamException(TeamExceptionType.UNAUTHORIZED_TEAM_LEADER);
        }

        // 팀 이름 중복 검사
        if (teamRepository.existsByTeamName(request.getTeamName())) {
            throw new TeamException(TeamExceptionType.ALREADY_EXIST_NAME);
        }

        team.setTeamName(request.getTeamName());
        team.setTeamCategory(request.getTeamCategory());
        team.setTeamStartDate(request.getTeamStartDate());
        team.setTeamRegion(request.getTeamRegion());
        team.setTeamAge(request.getTeamAge());
        team.setTeamSize(request.getTeamSize());
        team.setTeamDescription(request.getTeamDescription());

        teamRepository.save(team); // 수정된 팀 정보 저장
        log.info("팀 정보가 수정되었습니다. 팀 ID: {}", teamId);
    }

    // 팀원 추가
    @Transactional
    public String addMemberToTeam(String leaderEmail, Long teamId, String newMemberEmail) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(TeamExceptionType.TEAM_NOT_EXIST));

        // 팀장 조회
        Member leader = memberRepository.findByEmail(leaderEmail);
        if (leader == null) {
            throw new MemberException(MemberExceptionType.MEMBER_NOT_EXIST);
        } else if (!team.getTeamLeader().getId().equals(leader.getId())) {
            throw new TeamException(TeamExceptionType.UNAUTHORIZED_TEAM_LEADER);
        }

        // 추가할 멤버 조회
        Member newMember = memberRepository.findByEmail(newMemberEmail);
        if (newMember == null) {
            throw new MemberException(MemberExceptionType.MEMBER_NOT_EXIST);
        } else if (newMember.getTeam() != null && !newMember.getTeam().getId().equals(team.getId())) {
            throw new TeamException(TeamExceptionType.ALREADY_HAVE_TEAM);
        }

        // 팀에 새로운 멤버 추가
        team.getMembers().add(newMember);

        // 추가된 멤버의 팀 정보 설정
        newMember.setTeam(team);
        newMember.setRole("ROLE_USER"); // 기본 역할을 일반 사용자로 설정

        memberRepository.save(newMember); // 새로운 팀원 저장
        teamRepository.save(team); // 수정된 팀 정보 저장

        log.info("팀원 추가 완료. 팀 ID: {}, 팀원: {}", teamId, newMember.getName());
        return newMember.getName();
    }

    // 팀원 삭제
    @Transactional
    public String removeMemberFromTeam(String leaderEmail, Long teamId, Long memberId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(TeamExceptionType.TEAM_NOT_EXIST));

        // 팀장 조회
        Member leader = memberRepository.findByEmail(leaderEmail);
        if (leader == null) {
            throw new MemberException(MemberExceptionType.MEMBER_NOT_EXIST);
        } else if (!team.getTeamLeader().getId().equals(leader.getId())) {
            throw new TeamException(TeamExceptionType.UNAUTHORIZED_TEAM_LEADER);
        }

        // 삭제할 멤버 조회
        Member deleteMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_EXIST));

        // 팀에서 멤버가 있는지 확인
        if (deleteMember.getTeam() == null || !deleteMember.getTeam().getId().equals(team.getId())) {
            throw new TeamException(TeamExceptionType.NOT_YOUR_MEMBER);
        }

        // 팀에서 멤버 삭제
        team.getMembers().remove(deleteMember); // 팀에서 멤버 제거
        deleteMember.setTeam(null); // 멤버의 팀 정보 제거
        deleteMember.setRole("ROLE_USER"); // 역할을 기본값인 ROLE_USER로 설정

        // 수정된 멤버 정보 저장
        memberRepository.save(deleteMember);

        // 수정된 팀 정보 저장
        teamRepository.save(team);

        log.info("팀원 삭제 완료. 팀 ID: {}, 삭제된 팀원: {}", teamId, deleteMember.getName());
        return deleteMember.getName();
    }

    // 팀 삭제
    @Transactional
    public void deleteTeam(String leaderEmail, Long teamId) {
        // 팀 조회
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(TeamExceptionType.TEAM_NOT_EXIST));

        // 팀장 조회
        Member leader = memberRepository.findByEmail(leaderEmail);
        if (leader == null) {
            throw new MemberException(MemberExceptionType.MEMBER_NOT_EXIST);
        } else if (!team.getTeamLeader().getId().equals(leader.getId())) {
            throw new TeamException(TeamExceptionType.UNAUTHORIZED_TEAM_LEADER);
        }

        // 팀에 속한 멤버들의 팀 정보 제거
        for (Member member : team.getMembers()) {
            member.setTeam(null);  // 팀 정보를 null 로 설정하여 팀에서 제거
            member.setRole("ROLE_USER");  // 역할을 기본 USER 로 변경
        }

        // 멤버 정보 일괄 업데이트
        memberRepository.saveAll(team.getMembers());

        // 팀 삭제
        teamRepository.delete(team);

        // 팀 삭제 완료 로그
        log.info("팀 삭제 완료. 팀 이름: {}, 팀장: {}", team.getTeamName(), leader.getName());
    }

    // 특정 팀 조회
    public Team findTeamById(Long id) {
        // 게시글 ID로 상세 정보 조회
        return teamRepository.findById(id).orElse(null);  // 팀이 존재하지 않으면 null 반환
    }

    // 팀 이름으로 팀을 검색하는 메서드
    public List<Team> findTeamsByName(String teamName) {
        // 팀 이름을 포함한 팀을 검색 (부분 일치)
        return teamRepository.findByTeamNameContainingIgnoreCase(teamName);  // 대소문자 구분 없이 검색
    }

    // 사용자가 속한 팀 조회
    public Team findTeamByMemberEmail(String email) {
        // 이메일을 가진 사용자가 속한 팀을 찾음
        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            return null;  // 해당 이메일을 가진 사용자가 없다면 null 반환
        }

        // 사용자가 속한 팀을 반환
        return member.getTeam();  // Member 객체에 팀 정보가 포함되어 있다고 가정
    }

}
