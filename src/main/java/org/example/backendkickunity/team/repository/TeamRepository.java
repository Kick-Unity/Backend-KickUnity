package org.example.backendkickunity.team.repository;

import org.example.backendkickunity.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    boolean existsByTeamName(String name);

    // 팀 이름으로 팀을 검색하는 메서드
    List<Team> findByTeamNameContainingIgnoreCase(String teamName);
}
