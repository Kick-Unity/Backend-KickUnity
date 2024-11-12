package org.example.backendkickunity.board.service;


import org.example.backendkickunity.board.domain.Board;
import org.example.backendkickunity.board.domain.dto.AddArticleRequest;
import org.example.backendkickunity.board.domain.dto.UpdateArticleRequest;
import org.example.backendkickunity.board.repository.BoardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository){
        this.boardRepository = boardRepository;
    }

    public Board save(AddArticleRequest request){
        Board board = new Board(request.getTitle(), request.getContent());

        return boardRepository.save(request.toEntity());
    }

    public List<Board> findAll() {
        return boardRepository.findAll();
    }

    public Board findById(Long id){
        return boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    public void delete(Long id){
        boardRepository.deleteById(id);
    }

    @Transactional
    public Board update(long id, UpdateArticleRequest request){
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found" + id));

        board.update(request.getTitle(), request.getContent());
        return board;
    }
}
