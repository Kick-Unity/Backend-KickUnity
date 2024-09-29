package org.example.backendkickunity.repository;

import org.example.backendkickunity.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
