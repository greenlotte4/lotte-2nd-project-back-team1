package com.BackEndTeam1.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatTextResponseDTO {
    private String id;

    private UserDTO senderId;
    private String context;
    private int chatId;

    private LocalDateTime SendTime;
}

