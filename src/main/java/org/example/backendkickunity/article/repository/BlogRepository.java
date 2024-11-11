package org.example.backendkickunity.article.repository;

import org.example.backendkickunity.article.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Article, Long> {
}
