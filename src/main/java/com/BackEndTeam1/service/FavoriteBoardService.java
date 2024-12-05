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

import java.util.Optional;

@Log4j2
@AllArgsConstructor
@Service
public class FavoriteBoardService {

    private final UserRepository userRepository;
    private FavoriteBoardRepository favoriteBoardRepository;
    private BoardRepository boardRepository;



    public void toggleFavorite(Board boardId, Boolean isFavorite, User userId) {
        User user = userRepository.findById(String.valueOf(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Board board = (Board) boardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));

        Optional<FavoriteBoard> favoriteBoardOptional = favoriteBoardRepository.findByUserAndBoard(user, board);

        if (favoriteBoardOptional.isPresent()) {
            FavoriteBoard favoriteBoard = favoriteBoardOptional.get();
            favoriteBoard.setIsFavorite(isFavorite);
            favoriteBoardRepository.save(favoriteBoard);
        } else {
            FavoriteBoard newFavorite = FavoriteBoard.builder()
                    .user(user)
                    .board(board)
                    .isFavorite(isFavorite)
                    .build();
            favoriteBoardRepository.save(newFavorite);
        }
    }


}
