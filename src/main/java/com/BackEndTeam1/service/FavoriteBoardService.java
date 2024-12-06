package com.BackEndTeam1.service;

import com.BackEndTeam1.dto.FavoriteBoardDTO;
import com.BackEndTeam1.entity.Board;
import com.BackEndTeam1.entity.FavoriteBoard;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.BoardRepository;
import com.BackEndTeam1.repository.FavoriteBoardRepository;
import com.BackEndTeam1.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor
@Service
public class FavoriteBoardService {

    private final UserRepository userRepository;
    private FavoriteBoardRepository favoriteBoardRepository;
    private BoardRepository boardRepository;



    public void toggleFavorite(Board boardId, Boolean isFavorite, User userId) {
        User user = userRepository.findByUserId(userId.getUserId()) // User의 userId 필드 사용
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Board board = (Board) boardRepository.findByBoardId(boardId.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));

        Optional<FavoriteBoard> favoriteBoardOptional = favoriteBoardRepository.findByUserAndBoard(user, board);

        if (favoriteBoardOptional.isPresent()) {
            FavoriteBoard favoriteBoard = favoriteBoardOptional.get();
            favoriteBoard.setIsFavorite(!favoriteBoard.getIsFavorite()); // 상태 반전
            favoriteBoardRepository.save(favoriteBoard);
            System.out.println("Existing FavoriteBoard Updated: " + favoriteBoard);
        } else {
            FavoriteBoard newFavorite = FavoriteBoard.builder()
                    .user(user)
                    .board(board)
                    .isFavorite(isFavorite = true) // 기본값 설정
                    .build();
            favoriteBoardRepository.save(newFavorite);
            System.out.println("New FavoriteBoard Created: " + newFavorite);
        }
    }

    public List<FavoriteBoardDTO> getFavoriteBoards(String userId) {
        List<FavoriteBoard> favoriteBoards = favoriteBoardRepository.findByUser_UserId(userId);

        // FavoriteBoard를 FavoriteBoardDTO로 변환
        return favoriteBoards.stream().map(favoriteBoard -> {
            User user = favoriteBoard.getUser();
            Board board = favoriteBoard.getBoard();
            return new FavoriteBoardDTO(
                    Long.valueOf(board.getBoardId()),
                    user.getUserId(),
                    favoriteBoard.getIsFavorite()
            );
        }).collect(Collectors.toList());
    }

}
