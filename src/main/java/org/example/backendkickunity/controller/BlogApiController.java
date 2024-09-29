package org.example.backendkickunity.controller;

import org.example.backendkickunity.domain.article.Article;
import org.example.backendkickunity.domain.article.dto.AddArticleRequest;
import org.example.backendkickunity.domain.article.dto.ArticleResponse;
import org.example.backendkickunity.domain.article.dto.UpdateArticleRequest;
import org.example.backendkickunity.service.BlogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BlogApiController {
    private final BlogService blogService;
    public BlogApiController(BlogService blogService){
        this.blogService = blogService;
    }

    @PostMapping("api/articles")
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request){
        Article savedArticle = blogService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedArticle);
    }

    @GetMapping("api/articles")
    public ResponseEntity<List<ArticleResponse>> getArticles(){
        List<ArticleResponse> articles = blogService.findAll()
                .stream()
                .map(ArticleResponse::new)
                .toList();
        return ResponseEntity.status(HttpStatus.OK)
                .body(articles);
    }

    @GetMapping("api/articles/{id}")
    public ResponseEntity<ArticleResponse> getArticle(@PathVariable long id){
        Article articleResponse = blogService.findById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ArticleResponse(articleResponse));
    }

    @DeleteMapping("api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id){
        blogService.delete(id);

        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @PutMapping("api/articles/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable long id, @RequestBody UpdateArticleRequest request){
        Article article = blogService.update(id, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(article);
    }



}
