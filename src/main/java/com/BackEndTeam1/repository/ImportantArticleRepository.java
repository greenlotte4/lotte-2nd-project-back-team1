package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.ImportantArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImportantArticleRepository extends JpaRepository<ImportantArticle, Long> {

    Optional<ImportantArticle> findByUser_UserIdAndArticleId(String userId, Long articleId);

}
