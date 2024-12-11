package com.BackEndTeam1.controller;

import com.BackEndTeam1.dto.ChatDTO;
import com.BackEndTeam1.dto.ChatRequestDTO;
import com.BackEndTeam1.dto.ChatRoomDTO;
import com.BackEndTeam1.entity.ChatRoom;
import com.BackEndTeam1.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Controller
@RequestMapping("/message")
public class MessageController {
    private final MessageService messageService;

    @PostMapping("/newChannel")
    public ResponseEntity newChannel(@RequestBody ChatRequestDTO chatRequestDTO) {
        log.info("채널 추가 : " + chatRequestDTO);

        ChatDTO savedChatDTO = messageService.saveChannel(chatRequestDTO);


        chatRequestDTO.getMembers().forEach(userId ->
                messageService.saveChatRoom(savedChatDTO, userId)
        );

        // 결과 반환
        return ResponseEntity.status(HttpStatus.OK).body(savedChatDTO);
    }

    @PostMapping("/newDM")
    public ResponseEntity newDM(@RequestBody ChatRequestDTO chatRequestDTO) {
        log.info("DM 추가 : " + chatRequestDTO);

        ChatDTO savedChatDTO = messageService.saveChannel(chatRequestDTO);


        chatRequestDTO.getMembers().forEach(userId ->
                messageService.saveChatRoom(savedChatDTO, userId)
        );

        // 결과 반환
        return ResponseEntity.status(HttpStatus.OK).body(savedChatDTO);
    }

    @GetMapping("/rooms")
    public ResponseEntity getRooms(@RequestParam String userId) {
        List<ChatRoomDTO> chatRoomList = messageService.findByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(chatRoomList);
    }
}
