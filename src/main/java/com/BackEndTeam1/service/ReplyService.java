package com.BackEndTeam1.service;

import com.BackEndTeam1.entity.Comment;
import com.BackEndTeam1.entity.Reply;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.CommentRepository;
import com.BackEndTeam1.repository.ReplyRepository;
import com.BackEndTeam1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public Reply saveReply(Integer commentId, String userId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        Reply reply = new Reply();
        reply.setComment(comment);
        reply.setUser(user);
        reply.setContent(content);

        return replyRepository.save(reply);
    }

    // 특정 댓글의 답글 목록 조회
    public List<Reply> getRepliesByCommentId(Long commentId) {
        return replyRepository.findByComment_CommentId(commentId);
    }
}
