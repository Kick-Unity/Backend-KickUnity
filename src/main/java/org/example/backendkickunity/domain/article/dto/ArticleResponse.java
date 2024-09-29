package org.example.backendkickunity.domain.article.dto;

import lombok.Getter;
import org.example.backendkickunity.domain.article.Article;

@Getter
public class ArticleResponse {
    private String title;
    private String content;

    public ArticleResponse(Article article){
        this.title = article.getTitle();
        this.content = article.getContent();
    }
}
