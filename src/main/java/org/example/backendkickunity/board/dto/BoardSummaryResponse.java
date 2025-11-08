package org.example.backendkickunity.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardSummaryResponse {
    private Long id;         // 게시글 ID
    private String title;    // 게시글 제목
    private String content;  // 게시글 내용 (요약)
    private String createdDate; // 작성 일자 + 시간
}