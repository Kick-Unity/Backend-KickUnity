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
        Board board = Board.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .member(member)  // 작성자 정보 추가
                .category(request.getCategory())
                .build();

        return boardRepository.save(board);  // 게시글 저장
    }

    // 모든 게시글 조회
    public List<Board> findAll() {
        return boardRepository.findAll();
    }

    // 로그인한 회원 '내가 쓴 글' 조회
    public List<Board> findBoardsByLoginEmail(String email) {
        return boardRepository.findAllByMemberEmail(email);
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

    // 제목에 키워드가 포함된 게시글 검색
    public List<Board> searchBoards(String boardCategory, String keyword) {
        // String 값으로 들어온 category를 BoardCategory enum으로 변환
        BoardCategory category = BoardCategory.valueOf(boardCategory);

        if (keyword != null && !keyword.isEmpty()) {
            return boardRepository.findAllByCategoryAndTitleContainingOrCategoryAndContentContaining(category,keyword);  // 제목에 키워드 포함된 게시글만 검색
        }
        return findAll();  // 키워드가 없으면 모든 게시글 조회
    }

}
