package com.BackEndTeam1.controller;

import com.BackEndTeam1.dto.ChannelDTO;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Log4j2
@RequiredArgsConstructor
@Controller
@RequestMapping("/message")
public class MessageController {
    private final MessageService messageService;

    @PostMapping("/newChannel")
    public ResponseEntity newChannel(@RequestBody ChannelDTO channelDTO) {
        log.info("채널 추가 : " + channelDTO);


        ChannelDTO savedChannelDTO = messageService.saveChannel(channelDTO);
        // 결과 반환
        return ResponseEntity.status(HttpStatus.OK).body(savedChannelDTO);
    }
}
