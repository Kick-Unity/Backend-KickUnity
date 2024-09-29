package org.example.backendkickunity.repository;

import org.example.backendkickunity.domain.article.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Article, Long> {
}
