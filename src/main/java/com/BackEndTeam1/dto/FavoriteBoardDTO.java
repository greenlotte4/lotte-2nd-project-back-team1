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

    private Long boardId; // Board의 ID만 포함
    private String userId;  // User의 ID만 포함
    private boolean isFavorite;

    public FavoriteBoardDTO toDto(Board board, User user, boolean isFavorite) {
        return FavoriteBoardDTO.builder()
                .boardId(Long.valueOf(board.getBoardId())) // 엔티티에서 ID만 추출
                .userId(user.getUserId())   // 엔티티에서 ID만 추출
                .isFavorite(isFavorite)
                .build();
    }

    public Board toEntity(FavoriteBoardDTO dto) {
        // 필요한 경우 DTO를 엔티티로 변환하는 로직 추가
        // Board나 User를 별도로 조회해서 설정
        return null;
    }
}
