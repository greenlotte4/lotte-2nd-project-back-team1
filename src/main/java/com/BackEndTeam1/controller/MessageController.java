package com.BackEndTeam1.controller;

import com.BackEndTeam1.document.ChatTextDocument;
import com.BackEndTeam1.dto.*;
import com.BackEndTeam1.entity.ChatRoom;
import com.BackEndTeam1.service.FileService;
import com.BackEndTeam1.service.MessageService;
import com.BackEndTeam1.service.UserService;
import com.BackEndTeam1.service.mongo.ChatTextService;
import com.BackEndTeam1.util.CustomFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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


    private final CustomFileUtil customFileUtil;

    private final FileService fileService;
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

    @GetMapping("/room")
    public ResponseEntity getRoom(@RequestParam Integer chatId) {
        List<ChatRoomDTO> chatRoomList = messageService.findByChatId(chatId);
        log.info("getRoom 호출되었음" + chatRoomList);
        return ResponseEntity.status(HttpStatus.OK).body(chatRoomList);
    }

    @DeleteMapping("/room")
    public ResponseEntity<?> deleteRoom(@RequestParam int chatRoomId) {
        ChatRoomDTO chatRoomDTO = messageService.findChatRoomById(chatRoomId);

        if(chatRoomDTO.getChat().getDtype().equals("DM"))
        {
            try {
                boolean isLeft = messageService.deleteChat(chatRoomDTO.getChat().getChatId());
                if (isLeft) {
                    return ResponseEntity.ok("채팅방에서 성공적으로 나갔습니다.");
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("채팅방 나가기에 실패했습니다.");
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
            }

        }else{

            try {
                boolean isLeft = messageService.leaveChatRoom(chatRoomId);
                if (isLeft) {
                    return ResponseEntity.ok("채팅방에서 성공적으로 나갔습니다.");
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("채팅방 나가기에 실패했습니다.");
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
            }
        }

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

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("Uploading file");
        try {
            // 서비스 호출
            String fileDownloadUri = fileService.uploadMessageImage(file);
            return ResponseEntity.status(HttpStatus.OK).body(fileDownloadUri); // 성공 시 이미지 URL 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("업로드 실패: " + e.getMessage());
        }
    }

    @GetMapping("/img/{fileName}")
    public ResponseEntity<Resource> MessageImg(@PathVariable String fileName){

        log.info(fileName);

        return customFileUtil.getFile(fileName);
    }

    @GetMapping("/check/dm")
    public ResponseEntity<Boolean> MessageImg(@RequestParam String targetUserId,
                                              @RequestParam String userId){


        try {
            // 사용자 Plan 확인
            boolean isTargetUserPlanValid = userService.findByUserId(targetUserId).getPlan().getPlanId() != 1;
            boolean isUserPlanValid = userService.findByUserId(userId).getPlan().getPlanId() != 1;


            // DM 존재 여부 확인
            boolean isNewDMAllowed = messageService.checkDMCount(targetUserId)
                    && messageService.checkDMCount(userId);

            log.info("isTargetUserPlanValid : "+isTargetUserPlanValid);
            log.info("isUserPlanValid : "+isUserPlanValid);
            log.info("isNewDMAllowed : "+isNewDMAllowed);

            // 최종 반환 조건
            if (isNewDMAllowed) {
                // DM 허용이 가능하면 Plan 상태에 상관없이 true 반환
                return ResponseEntity.ok(true);
            } else if (!isTargetUserPlanValid || !isUserPlanValid) {
                // Plan이 유효하지 않고, DM 허용도 불가능한 경우 false 반환
                return ResponseEntity.ok(false);
            }
            // 모든 조건 만족 시 true 반환
            return ResponseEntity.ok(true);


        } catch (Exception e) {
            // 에러 발생 시 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }
}
