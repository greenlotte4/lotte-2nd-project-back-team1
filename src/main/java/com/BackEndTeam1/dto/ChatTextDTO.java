package com.BackEndTeam1.dto;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatTextDTO {
    private String id;

    private String senderId;
    private String context;
    private int chatId;

    private LocalDateTime SendTime;
}
