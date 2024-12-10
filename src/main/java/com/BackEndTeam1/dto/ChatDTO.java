package com.BackEndTeam1.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatDTO {
    private Integer chatId;

    private String roomName;

    private String dtype; // Enum: channel, dm
}
