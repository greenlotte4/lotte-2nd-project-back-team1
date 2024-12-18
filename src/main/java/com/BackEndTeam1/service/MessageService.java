package com.BackEndTeam1.service;

import com.BackEndTeam1.dto.ChatDTO;
import com.BackEndTeam1.dto.ChatRequestDTO;
import com.BackEndTeam1.dto.ChatRoomDTO;
import com.BackEndTeam1.dto.UserDTO;
import com.BackEndTeam1.entity.Chat;
import com.BackEndTeam1.entity.ChatRoom;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.ChatRepository;
import com.BackEndTeam1.repository.ChatRoomRepository;
import com.BackEndTeam1.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@AllArgsConstructor
@Service
public class MessageService {
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public ChatDTO saveChannel(ChatRequestDTO chatRequestDTO) {

        Chat chat = modelMapper.map(chatRequestDTO, Chat.class);

        Chat savedChat = chatRepository.save(chat);

        return modelMapper.map(savedChat, ChatDTO.class);
    }

    public void saveChatRoom(ChatDTO chatDTO, String userId) {

        ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
        chatRoomDTO.setChat(chatDTO);

        ChatRoom chatRoom = modelMapper.map(chatRoomDTO, ChatRoom.class);
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            chatRoom.setUser(user.get());
        }
        chatRoomRepository.save(chatRoom);
    }
    public List<ChatRoomDTO> findByUserId(String userId) {
        List<ChatRoomDTO> chatRoomDTOS = new ArrayList<>();
        for (ChatRoom chatRoom : chatRoomRepository.findByUserId(userId)) {
            chatRoomDTOS.add(modelMapper.map(chatRoom, ChatRoomDTO.class));
        }
        return chatRoomDTOS;
    }
    public List<ChatRoomDTO> findByChatId(Integer chatId) {
        List<ChatRoom> chatRoomList = chatRoomRepository.findByChatChatId(chatId);
        List<ChatRoomDTO> chatRoomDTOS = new ArrayList<>();
        for (ChatRoom chatRoom : chatRoomList) {
            chatRoomDTOS.add(modelMapper.map(chatRoom, ChatRoomDTO.class));

        }
        return chatRoomDTOS;
    }
}
