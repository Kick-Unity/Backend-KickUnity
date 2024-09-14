package org.example.backedkickunity.repository;

import org.example.backedkickunity.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
