package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.BoardArticle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardArticleRepository extends JpaRepository<BoardArticle, Integer> {
}
