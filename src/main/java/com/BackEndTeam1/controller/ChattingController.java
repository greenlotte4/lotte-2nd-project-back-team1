package com.BackEndTeam1.controller;

import com.BackEndTeam1.dto.ChatTextDTO;
import com.BackEndTeam1.dto.ChatTextResponseDTO;
import com.BackEndTeam1.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Log4j2
@Controller
@RequiredArgsConstructor
public class ChattingController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;

    @MessageMapping("/chat/send")
    public void sendMessage(ChatTextDTO chatTextDTO) {
        try {
            log.info("채팅 받음: " + chatTextDTO);
            String destination = "/sub/room/" + chatTextDTO.getChatId();
            //Response매핑
            ChatTextResponseDTO chatTextResponseDTO = new ChatTextResponseDTO();
            chatTextResponseDTO.setChatId(chatTextDTO.getChatId());
            chatTextResponseDTO.setContext(chatTextDTO.getContext());
            chatTextResponseDTO.setSendTime(chatTextDTO.getSendTime());
            chatTextResponseDTO.setSenderId(userService.findByUserId(chatTextDTO.getSenderId()));

            messagingTemplate.convertAndSend(destination, chatTextResponseDTO);


            log.info("메시지 전송 완료: " + destination);
        } catch (Exception e) {
            log.error("메시지 처리 중 오류 발생: ", e);
        }
    }
}
