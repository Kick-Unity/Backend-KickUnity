package org.example.backendkickunity.board.dto;

import lombok.Getter;
import org.example.backendkickunity.board.domain.Board;

@Getter
public class BoardResponse {
    private String title;
    private String content;

    public BoardResponse(Board board){
        this.title = board.getTitle();
        this.content = board.getContent();
    }
}
