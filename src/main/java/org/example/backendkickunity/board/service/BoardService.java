package org.example.backendkickunity.article.service;


import org.example.backendkickunity.article.domain.Article;
import org.example.backendkickunity.article.domain.dto.AddArticleRequest;
import org.example.backendkickunity.article.domain.dto.UpdateArticleRequest;
import org.example.backendkickunity.article.repository.BlogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BlogService {

    private final BlogRepository blogRepository;

    public BlogService(BlogRepository blogRepository){
        this.blogRepository = blogRepository;
    }

    public Article save(AddArticleRequest request){
        Article article = new Article(request.getTitle(), request.getContent());

        return blogRepository.save(request.toEntity());
    }

    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    public Article findById(Long id){
        return blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    public void delete(Long id){
        blogRepository.deleteById(id);
    }

    @Transactional
    public Article update(long id, UpdateArticleRequest request){
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found" + id));

        article.update(request.getTitle(), request.getContent());
        return article;
    }
}
