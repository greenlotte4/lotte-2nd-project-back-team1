package com.BackEndTeam1.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteBoardDTO {

    private Long boardId;
    private boolean isFavorite;
}
