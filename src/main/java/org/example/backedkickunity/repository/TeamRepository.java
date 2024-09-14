package org.example.backedkickunity.repository;

import org.example.backedkickunity.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
