package org.example.backendkickunity.team.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.backendkickunity.auth.service.AuthService;
import org.example.backendkickunity.team.domain.Team;
import org.example.backendkickunity.team.dto.*;
import org.example.backendkickunity.team.service.TeamService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/team")
public class TeamApiController {

    private final TeamService teamService;
    private final AuthService authService;

    public TeamApiController(TeamService teamService, AuthService authService) {
        this.teamService = teamService;
        this.authService = authService;
    }

    // 팀 생성 - PM 테스트 완료
    @PostMapping("/create")
    public ResponseEntity<Long> createTeam(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody AddTeamRequest request) {

        // Authorization header 에서 로그인 회원 이메일 추출
        String email = authService.extractEmailFromAuthorizationHeader(authorizationHeader);

        // 팀 생성
        Long teamId = teamService.createTeam(email, request);

        log.info("팀 생성 완료. 팀 이름: {}", request.getTeamName());
        return ResponseEntity.status(HttpStatus.CREATED).body(teamId);
    }

    // 팀 정보 수정 - PM 테스트 완료
    @PutMapping("/{teamId}")
    public ResponseEntity<String> updateTeam(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable Long teamId, @RequestBody UpdateTeamRequest request) {
        // Authorization header 에서 로그인 회원 이메일 추출
        String email = authService.extractEmailFromAuthorizationHeader(authorizationHeader);

        // 팀 정보 수정
        teamService.updateTeam(email, teamId, request);

        log.info("팀 정보 수정 완료. 팀 ID: {}", teamId);
        return ResponseEntity.ok("팀 정보가 성공적으로 수정되었습니다.");
    }

    // 팀원 추가
    @PostMapping("/{teamId}/addMember")
    public ResponseEntity<String> addMemberToTeam(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable Long teamId, @RequestBody AddTeamMemberRequest request) {

        // Authorization header 에서 로그인 회원 이메일 추출
        String email = authService.extractEmailFromAuthorizationHeader(authorizationHeader);

        // 멤버 추가, 추가된 멤버 정보 저장
        String newMemberName = teamService.addMemberToTeam(email, teamId, request.getMemberEmail());

        log.info("팀원 추가 완료. 팀 ID: {},  추가된 팀원: {}", teamId, newMemberName);
        return ResponseEntity.status(HttpStatus.OK).body(newMemberName);
    }

    // 팀원 삭제
    @DeleteMapping("/{teamId}/removeMember/{memberId}")
    public ResponseEntity<String> removeMemberFromTeam(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable Long teamId, @PathVariable Long memberId) {

        // Authorization header 에서 로그인 회원 이메일 추출
        String email = authService.extractEmailFromAuthorizationHeader(authorizationHeader);

        // 멤버 삭제, 삭제된 멤버 정보 저장
        String deletedMemberName = teamService.removeMemberFromTeam(email, teamId, memberId);

        log.info("팀원 삭제 완료. 팀 ID: {}, 삭제된 팀원: {}", teamId, deletedMemberName);
        return ResponseEntity.status(HttpStatus.OK).body(deletedMemberName);
    }

    // 팀 삭제
    @DeleteMapping("/{teamId}")
    public ResponseEntity<String> deleteTeam(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable Long teamId) {
        // Authorization header 에서 로그인 회원 이메일 추출
        String email = authService.extractEmailFromAuthorizationHeader(authorizationHeader);

        // 팀 삭제
        teamService.deleteTeam(email, teamId);

        return ResponseEntity.status(HttpStatus.OK).body("팀 삭제가 완료되었습니다.");
    }

    // 팀 이름으로 팀 검색
    @GetMapping("/searchTeam")
    public ResponseEntity<List<TeamSummaryResponse>> searchTeamByName(@RequestParam String teamName) {
        // 팀 이름으로 팀 검색
        List<Team> teams = teamService.findTeamsByName(teamName);

        if (teams.isEmpty()) {
            log.warn("해당 이름을 가진 팀이 존재하지 않습니다.");
            return ResponseEntity.notFound().build();        }

        // 팀 리스트를 TeamResponse DTO로 변환하여 반환
        List<TeamSummaryResponse> teamSummaryResponse = teams.stream()
                .map(team -> new TeamSummaryResponse(
                        team.getId(),
                        team.getTeamName(),
                        team.getTeamCategory(),
                        team.getTeamRegion()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(teamSummaryResponse);
    }

    // 팀 상세 정보 확인 - PM 테스트 확인
    @GetMapping("/{teamId}")
    public ResponseEntity<TeamDetailResponse> getTeam(@PathVariable Long teamId) {
        log.info("게시글 상세 조회 요청을 받았습니다. 게시글 ID: {}", teamId);

        Team team = teamService.findTeamById(teamId);

        if (team == null) {
            log.warn("팀 ID {}를 찾을 수 없습니다.", teamId);
            return ResponseEntity.notFound().build();
        }

        // Team 엔티티를 TeamResponse DTO로 변환
        TeamDetailResponse teamDetailResponse = new TeamDetailResponse(
                team.getId(),
                team.getTeamName(),
                team.getTeamCategory(),
                team.getTeamStartDate(),
                team.getTeamRegion(),
                team.getTeamAge(),
                team.getTeamSize(),
                team.getTeamDescription());

        return ResponseEntity.ok(teamDetailResponse);
    }
}
