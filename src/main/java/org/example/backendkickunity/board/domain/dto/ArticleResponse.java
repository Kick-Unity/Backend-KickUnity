package org.example.backendkickunity.board.domain.dto;

import lombok.Getter;
import org.example.backendkickunity.board.domain.Board;

@Getter
public class ArticleResponse {
    private String title;
    private String content;

    public ArticleResponse(Board board){
        this.title = board.getTitle();
        this.content = board.getContent();
    }
}
