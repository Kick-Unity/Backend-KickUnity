package org.example.backendkickunity.board.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.backendkickunity.auth.AuthService;
import org.example.backendkickunity.board.domain.Board;
import org.example.backendkickunity.board.dto.AddBoardRequest;
import org.example.backendkickunity.board.dto.SearchBoardRequest;
import org.example.backendkickunity.board.dto.UpdateBoardRequest;
import org.example.backendkickunity.board.service.BoardService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/board")
public class BoardApiController {

    private final BoardService boardService;
    private final AuthService authService;

    public BoardApiController(BoardService boardService, AuthService authService) {
        this.boardService = boardService;
        this.authService = authService;
    }

    // 게시글 등록
    @PostMapping
    public ResponseEntity<Board> addBoard(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody AddBoardRequest request) {
        // Authorization header에서 로그인 회원 이메일 추출
        String email = authService.extractEmailFromAuthorizationHeader(authorizationHeader);
        log.info("사용자 이메일 '{}'으로 게시글 등록 요청을 받았습니다.", email);

        Board savedBoard = boardService.save(email, request);
        log.info("게시글이 성공적으로 생성되었습니다. 게시글 ID: {}", savedBoard.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBoard);
    }


    // 카테고리 별(게시판 별) 게시글 조회
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Board>> getBoardsByCategory(@PathVariable String category) {
        log.info("카테고리 '{}' 게시글 조회 요청을 받았습니다.", category);

        List<Board> boards = boardService.findBoardsByCategory(category);

        if (boards.isEmpty()) {
            log.warn("카테고리 '{}'의 게시글이 없습니다.", category);
            return ResponseEntity.noContent().build();
        }

        log.info("카테고리 '{}'의 게시글 {}개가 성공적으로 조회되었습니다.", category, boards.size());
        return ResponseEntity.ok(boards);
    }

    // 로그인한 회원의 게시글 -> '내가 쓴 글'  조회
    @GetMapping("/myBoards")
    public ResponseEntity<List<Board>> getMyBoards(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        // Authorization header에서 로그인 회원 이메일 추출
        String email = authService.extractEmailFromAuthorizationHeader(authorizationHeader);
        log.info("사용자 이메일 '{}'으로 게시글 조회 요청을 받았습니다.", email);

        List<Board> boards = boardService.findBoardsByLoginEmail(email);

        if (boards.isEmpty()) {
            log.warn("사용자 '{}'의 게시글이 없습니다.", email);
            return ResponseEntity.noContent().build();
        }

        log.info("사용자 '{}'의 게시글 {}개가 성공적으로 조회되었습니다.", email, boards.size());
        return ResponseEntity.ok(boards);
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable Long id) {

        // Authorization header에서 로그인 회원 이메일 추출
        String email = authService.extractEmailFromAuthorizationHeader(authorizationHeader);
        log.info("게시글 삭제 요청을 받았습니다. 게시글 ID: {}, 현재 로그인 사용자 이메일: {}", id, email);
        boolean isDeleted = boardService.delete(email, id);

        if (isDeleted) {
            log.info("게시글 ID {}가 성공적으로 삭제되었습니다.", id);
            return ResponseEntity.noContent().build();
        } else {
            log.warn("게시글 ID {}를 찾을 수 없습니다. 삭제 실패.", id);
            return ResponseEntity.notFound().build();
        }
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<Board> updateBoard(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable Long id, @RequestBody UpdateBoardRequest request) {
        // Authorization header에서 로그인 회원 이메일 추출
        String email = authService.extractEmailFromAuthorizationHeader(authorizationHeader);
        log.info("게시글 수정 요청을 받았습니다. 게시글 ID: {}, 현재 로그인 사용자 이메일: {}", id, email);

        Board updatedBoard = boardService.update(email, id, request);  // 예외가 발생하면 BoardException 처리

        log.info("게시글 ID {}가 성공적으로 수정되었습니다.", id);
        return ResponseEntity.ok(updatedBoard);
    }

    // 제목에 포함된 키워드로 게시글 검색
    @GetMapping("/search")
    public ResponseEntity<List<Board>> searchBoards(@RequestParam SearchBoardRequest request) {
        log.info("게시글 제목 키워드 '{}'로 검색 요청을 받았습니다.", request.getKeyword());
        List<Board> boards = boardService.searchBoards(request.getCategory(), request.getKeyword());

        if (boards.isEmpty()) {
            log.warn("키워드 '{}'로 검색한 게시글이 없습니다.", request.getKeyword());
            return ResponseEntity.noContent().build();
        }

        log.info("키워드 '{}'로 검색한 게시글 {}개가 성공적으로 조회되었습니다.", request.getKeyword(), boards.size());
        return ResponseEntity.ok(boards);
    }
}
