package org.example.backendkickunity.board.controller;

import org.example.backendkickunity.auth.AuthService;
import org.example.backendkickunity.board.domain.Board;
import org.example.backendkickunity.board.domain.dto.AddBoardRequest;
import org.example.backendkickunity.board.domain.dto.UpdateBoardRequest;
import org.example.backendkickunity.board.service.BoardService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;
    private final AuthService authService;


    public BoardController(BoardService boardService, AuthService authService) {
        this.boardService = boardService;
        this.authService = authService;
    }

    // 게시글 등록
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Board addBoard(@RequestBody AddBoardRequest request) {
        return boardService.save(request);
    }

    // 모든 게시글 조회
    @GetMapping
    public List<Board> getAllBoards() {
        return boardService.findAll();
    }

    // 로그인한 회원의 게시글 조회
    @GetMapping("/myBoards")
    public List<Board> getMyBoards(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        // Authorization header 에서 로그인 회원 이메일 추출
        String email = authService.extractEmailFromAuthorizationHeader(authorizationHeader);

        return boardService.findBoardsByLoginEmail(email);  // 로그인한 회원의 게시글만 조회
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBoard(@PathVariable Long id) {
        boardService.delete(id);
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public Board updateBoard(@PathVariable Long id, @RequestBody UpdateBoardRequest request) {
        return boardService.update(id, request);
    }

    // 제목에 포함된 키워드로 게시글 검색
    @GetMapping("/search")
    public List<Board> searchBoards(@RequestParam String titleKeyword) {
        return boardService.searchBoards(titleKeyword);
    }
}
