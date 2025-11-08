package org.example.backendkickunity.board.repository;

import org.example.backendkickunity.board.domain.Board;
import org.example.backendkickunity.board.domain.BoardCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 모든 게시글을 최신순으로 조회
    List<Board> findAllByOrderByCreatedDateDesc();

    // 카테고리별 게시글을 최신순으로 조회
    List<Board> findAllByCategoryOrderByCreatedDateDesc(BoardCategory category);

    // 제목 또는 내용에 키워드가 포함된 게시글을 최신순으로 조회 (카테고리별)
    @Query("SELECT b FROM Board b WHERE b.category = :category " +
            "AND (b.title LIKE %:keyword% OR b.content LIKE %:keyword%) " +
            "ORDER BY b.createdDate DESC")
    List<Board> findAllByCategoryAndTitleContainingOrCategoryAndContentContaining(BoardCategory category, String keyword);

    // 제목 또는 내용에 키워드가 포함된 게시글을 최신순으로 조회
    @Query("SELECT b FROM Board b WHERE (b.title LIKE %:keyword% OR b.content LIKE %:keyword%) " +
            "ORDER BY b.createdDate DESC")
    List<Board> findAllByTitleContainingOrContentContaining(String keyword);

    // 로그인한 회원의 이메일을 기준으로 게시글 조회 (최신순 정렬)
    List<Board> findAllByMemberEmailOrderByCreatedDateDesc(String email);
}

