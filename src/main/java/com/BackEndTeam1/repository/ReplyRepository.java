package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Integer> {

    List<Reply> findByComment_CommentId(Long commentId);
}
