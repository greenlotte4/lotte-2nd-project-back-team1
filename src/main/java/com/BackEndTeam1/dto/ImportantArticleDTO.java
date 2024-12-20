package com.BackEndTeam1.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImportantArticleDTO {

    private Long importantId;
    private String userId;
    private Long articleId;
    private String title;
    private String content;
    private String boardName;
    private Date boardCreatedAt;
}
