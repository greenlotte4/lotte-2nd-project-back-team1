package com.BackEndTeam1.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MoveArticlesDTO {

    private List<Long> articleIds;
    private Long boardId;
}
