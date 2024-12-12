package com.BackEndTeam1.service.mongo;

import com.BackEndTeam1.document.ChatTextDocument;
import com.BackEndTeam1.dto.ChatTextDTO;
import com.BackEndTeam1.repository.mongo.ChatTextRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
public class ChatTextService {
    private final ChatTextRepository chatTextRepository;
    private final ModelMapper modelMapper;

    public List<ChatTextDTO> findByRoomId(int roomId) {
        List<ChatTextDocument> chatList = chatTextRepository.findByChatId(roomId);

        List<ChatTextDTO> chatTextDTOList = new ArrayList<>();
        for (ChatTextDocument chatTextDocument : chatList) {
            chatTextDTOList.add(modelMapper.map(chatTextDocument, ChatTextDTO.class));
        }
        return chatTextDTOList;

    }
    public ChatTextDTO insertChatText(ChatTextDTO chatTextDTO) {
        ChatTextDocument chatTextDocument = modelMapper.map(chatTextDTO, ChatTextDocument.class);
        ChatTextDocument savedChatTextDocument = chatTextRepository.save(chatTextDocument);
        return modelMapper.map(savedChatTextDocument, ChatTextDTO.class);
    }



    public String getLastChat(int chatId) {
        return chatTextRepository.findByChatId(chatId).stream()
                .max(Comparator.comparing(ChatTextDocument::getSendTime))
                .map(ChatTextDocument::getContext)
                .orElse("채팅 메시지가 없습니다.");
    }

}
