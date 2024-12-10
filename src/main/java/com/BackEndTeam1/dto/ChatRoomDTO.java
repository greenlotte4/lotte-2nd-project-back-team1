package com.BackEndTeam1.dto;

import com.BackEndTeam1.entity.Chat;
import com.BackEndTeam1.entity.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomDTO {
    private Integer chatRoomId;

    private UserDTO user;

    private ChatDTO chat;
}
