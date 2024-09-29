package org.example.backendkickunity.repository;

import org.example.backendkickunity.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
