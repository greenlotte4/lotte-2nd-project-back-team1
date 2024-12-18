package com.BackEndTeam1.dto;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDTO {

    private String userId;
    private Integer commentId;
    private String content;
    private String createdAt;


    public CommentDTO(Integer commentId, String username, String content, Timestamp createdAt) {
        this.userId = username;
        this.content = content;
        this.createdAt = createdAt.toString();
        this.commentId = commentId;
    }
}
