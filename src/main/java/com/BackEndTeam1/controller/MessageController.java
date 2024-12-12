package com.BackEndTeam1.controller;

import com.BackEndTeam1.document.ChatTextDocument;
import com.BackEndTeam1.dto.*;
import com.BackEndTeam1.entity.ChatRoom;
import com.BackEndTeam1.service.MessageService;
import com.BackEndTeam1.service.UserService;
import com.BackEndTeam1.service.mongo.ChatTextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Controller
@RequestMapping("/message")
public class MessageController {
    private final MessageService messageService;
    private final ChatTextService chatTextService;
    private final UserService userService;
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

    @GetMapping("/chat")
    public ResponseEntity getChat(@RequestParam int roomId) {
        List<ChatTextDTO> chatList = chatTextService.findByRoomId(roomId);
        List<ChatTextResponseDTO> chatTextResponseDTOList = chatList.stream().map(chat -> ChatTextResponseDTO.builder()
                .id(chat.getId())
                .senderId(userService.findByUserId(chat.getSenderId()))// UserDTO 변환
                .context(chat.getContext())
                .chatId(chat.getChatId())
                .SendTime(chat.getSendTime())
                .build()
        )
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(chatTextResponseDTOList);
    }

    @PostMapping("/chat")
    public ResponseEntity saveChat(@RequestBody ChatTextDTO chatTextDTO) {
        ChatTextDTO savedChatTextDTO = chatTextService.insertChatText(chatTextDTO);

        if(savedChatTextDTO != null) {
            return ResponseEntity.ok().build();
        }
        else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/lastchat")
    public ResponseEntity getLastChat(@RequestParam int roomId) {
        String lastChat = chatTextService.getLastChat(roomId);
        log.info(lastChat);
        return ResponseEntity.status(HttpStatus.OK).body(lastChat);
    }
}
