package org.example.backendkickunity.board.repository;

import org.example.backendkickunity.board.domain.Board;
import org.example.backendkickunity.board.domain.BoardCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 로그인한 회원의 이메일을 기준으로 게시글을 조회
    List<Board> findAllByMemberEmail(String email);

    // 운동 종목별 게시글 조회
    List<Board> findAllByCategory(BoardCategory category);

    // 제목 또는 내용에 키워드가 포함된 게시글 조회 (카테고리별로 필터링)
    @Query("SELECT b FROM Board b WHERE b.category = :category " +
            "AND (b.title LIKE %:keyword% OR b.content LIKE %:keyword%)")
    List<Board> findAllByCategoryAndTitleContainingOrCategoryAndContentContaining(BoardCategory category, String keyword);

    // 키워드만으로 게시글 조회 (카테고리가 없는 경우)
    @Query("SELECT b FROM Board b WHERE b.title LIKE %:keyword% OR b.content LIKE %:keyword%")
    List<Board> findAllByTitleContainingOrContentContaining(String keyword);
}
