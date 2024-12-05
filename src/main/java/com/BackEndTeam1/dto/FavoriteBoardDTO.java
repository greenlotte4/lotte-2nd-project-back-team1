package com.BackEndTeam1.dto;

import com.BackEndTeam1.entity.Board;
import com.BackEndTeam1.entity.User;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteBoardDTO {

    private Board boardId;
    private User userId;
    private boolean isFavorite;
}
