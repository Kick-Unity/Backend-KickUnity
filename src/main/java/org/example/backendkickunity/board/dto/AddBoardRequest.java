package org.example.backendkickunity.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backendkickunity.board.domain.Board;
import org.example.backendkickunity.board.domain.BoardCategory;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddBoardRequest {
    private String title;
    private String content;
    private BoardCategory category;

    public Board toEntity(){
        return Board.builder()
                .title(title)
                .content(content)
                .category(category)
                .build();
    }

}
