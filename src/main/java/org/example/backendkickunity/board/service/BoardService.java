package org.example.backendkickunity.board.service;

import org.example.backendkickunity.board.domain.Board;
import org.example.backendkickunity.board.domain.BoardCategory;
import org.example.backendkickunity.board.dto.AddBoardRequest;
import org.example.backendkickunity.board.dto.UpdateBoardRequest;
import org.example.backendkickunity.board.exception.BoardException;
import org.example.backendkickunity.board.exception.BoardExceptionType;
import org.example.backendkickunity.board.repository.BoardRepository;
import org.example.backendkickunity.member.domain.Member;
import org.example.backendkickunity.member.exception.MemberException;
import org.example.backendkickunity.member.exception.MemberExceptionType;
import org.example.backendkickunity.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    public BoardService(BoardRepository boardRepository, MemberRepository memberRepository) {
        this.boardRepository = boardRepository;
        this.memberRepository = memberRepository;
    }

    // 게시글 등록
    public Board save(String email, AddBoardRequest request) {
        // 작성자 정보(Member) 조회
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new MemberException(MemberExceptionType.MEMBER_NOT_EXIST);
        }

        // Board 객체 생성
        Board board = Board.builder()  // Builder 패턴 사용
                .title(request.getTitle())
                .content(request.getContent())
                .member(member)
                .category(request.getCategory())
                .build();

        return boardRepository.save(board);  // 게시글 저장
    }

    // 모든 게시글 조회 (최신순으로 정렬)
    public List<Board> findAllBoards() {
        return boardRepository.findAllByOrderByCreatedDateDesc();
    }

    // 게시판 카테고리에 맞는 게시글 리스트를 조회 (제목과 내용만 필요, 최신순 정렬)
    public List<Board> findBoardsByCategory(String category) {
        if ("ALL".equalsIgnoreCase(category)) {
            return findAllBoards(); // "ALL"일 경우 모든 게시글을 최신순으로 조회
        }
        try {
            BoardCategory boardCategory = BoardCategory.valueOf(category.toUpperCase());
            return boardRepository.findAllByCategoryOrderByCreatedDateDesc(boardCategory); // 특정 카테고리 내에서 최신순으로 조회
        } catch (IllegalArgumentException e) {
            throw new BoardException(BoardExceptionType.INVALID_CATEGORY); // 잘못된 카테고리 값에 대한 처리
        }
    }

    // 특정 게시글 조회
    public Board findBoardById(Long id) {
        // 게시글 ID로 상세 정보 조회
        return boardRepository.findById(id).orElse(null);  // 게시글이 존재하지 않으면 null 반환
    }

    // 로그인한 회원 '내가 쓴 글' 조회 (최신순으로 정렬)
    public List<Board> findBoardsByLoginEmail(String email) {
        return boardRepository.findAllByMemberEmailOrderByCreatedDateDesc(email);
    }

    // 게시글 수정
    @Transactional
    public Board update(String email, Long id, UpdateBoardRequest request) {

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(BoardExceptionType.BOARD_NOT_EXIST));  // 게시글이 없으면 예외를 던짐

        // 로그인한 사용자 이메일과 게시글 작성자 이메일을 비교
        if (!board.getMember().getEmail().equals(email)) {
            throw new BoardException(BoardExceptionType.BOARD_UNAUTHORIZED_UPDATE);  // 권한이 없으면 예외를 던짐
        }

        board.update(request.getTitle(), request.getContent());  // 게시글 내용 수정

        return board;
    }

    // 게시글 삭제
    @Transactional
    public boolean delete(String email, Long id) {

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(BoardExceptionType.BOARD_NOT_EXIST));  // 게시글이 없으면 예외를 던짐

        // 로그인한 사용자 이메일과 게시글 작성자 이메일을 비교
        if (!board.getMember().getEmail().equals(email)) {
            throw new BoardException(BoardExceptionType.BOARD_UNAUTHORIZED_DELETE);  // 권한이 없으면 예외를 던짐
        }

        boardRepository.delete(board);  // 게시글 삭제
        return true;  // 삭제 성공
    }

    // 제목에 키워드가 포함된 게시글 검색 (최신순 정렬)
    public List<Board> searchBoards(String boardCategory, String keyword) {
        BoardCategory category = null;

        // BoardCategory enum을 통한 카테고리 필터링
        if (boardCategory != null && !boardCategory.isEmpty()) {
            try {
                category = BoardCategory.valueOf(boardCategory.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BoardException(BoardExceptionType.INVALID_CATEGORY);
            }
        }

        if (category != null) {
            // 카테고리가 지정되면 해당 카테고리 내에서 키워드 검색 (최신순 정렬)
            return boardRepository.findAllByCategoryAndTitleContainingOrCategoryAndContentContaining(category, keyword);
        } else {
            // 카테고리가 지정되지 않으면 모든 게시글에서 키워드 검색 (최신순 정렬)
            return boardRepository.findAllByTitleContainingOrContentContaining(keyword);
        }
    }
}
