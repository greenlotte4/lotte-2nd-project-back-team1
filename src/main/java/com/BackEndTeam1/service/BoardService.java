package com.BackEndTeam1.service;

import com.BackEndTeam1.dto.BoardDTO;
import com.BackEndTeam1.dto.PageRequestDTO;
import com.BackEndTeam1.dto.PageResponseDTO;
import com.BackEndTeam1.entity.Board;
import com.BackEndTeam1.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public List<BoardDTO> getAllBoards() {
        return boardRepository.findAll().stream()
                .map(board -> BoardDTO.builder()
                        .board_id(board.getBoardId())
                        .board_name(board.getBoardName())
                        .max_collaborators(board.getMaxCollaborators() != null ? board.getMaxCollaborators() : 3)
                        .created_at(board.getCreatedAt() != null ? board.getCreatedAt().toString() : null)
                        .updated_at(board.getUpdatedAt() != null ? board.getUpdatedAt().toString() : null)
                        .user_id(board.getUser() != null ? board.getUser().getUserId() : null)
                        .build())
                .collect(Collectors.toList());
    }

    public String getBoardNameById(Long boardId) {
        return boardRepository.findById(boardId)
                .map(Board::getBoardName)
                .orElse("Unknown Board");
    }

}
