package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.Reply;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Integer> {

    List<Reply> findByComment_CommentId(Long commentId);

    @Transactional
    void deleteByComment_CommentId(Integer commentId);
}
