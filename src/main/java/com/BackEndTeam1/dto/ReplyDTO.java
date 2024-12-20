package com.BackEndTeam1.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyDTO {

    private String userId;   // 답글 작성자 ID
    private String content;  // 답글 내용
    private int articleId;
}
