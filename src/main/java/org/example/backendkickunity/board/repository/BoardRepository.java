package org.example.backendkickunity.board.repository;

import org.example.backendkickunity.board.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
