package com.BackEndTeam1.dto;

import lombok.*;

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
}
