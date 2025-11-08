package org.example.backendkickunity.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardDetailResponse {

    private Long id;          // 게시글 ID
    private String title;     // 게시글 제목
    private String content;   // 게시글 내용
    private String authorName; // 작성자 이름
    private String category;   // 게시판 카테고리
    private String createdDate;  // 게시글 작성 일자 + 시간
}
