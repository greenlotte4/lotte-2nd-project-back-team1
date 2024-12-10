package com.BackEndTeam1.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRequestDTO {

    private String roomName;
    private String dtype; // Enum: channel, dm
    private List<String> members;
}
