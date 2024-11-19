package org.example.backendkickunity.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SearchBoardRequest {

    private String category;
    private String keyword;
}

