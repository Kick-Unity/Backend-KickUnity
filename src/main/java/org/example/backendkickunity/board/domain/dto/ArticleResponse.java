package org.example.backendkickunity.article.domain.dto;

import lombok.Getter;
import org.example.backendkickunity.article.domain.Article;

@Getter
public class ArticleResponse {
    private String title;
    private String content;

    public ArticleResponse(Article article){
        this.title = article.getTitle();
        this.content = article.getContent();
    }
}
