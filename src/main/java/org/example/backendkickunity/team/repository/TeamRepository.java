package org.example.backendkickunity.team.repository;

import org.example.backendkickunity.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
