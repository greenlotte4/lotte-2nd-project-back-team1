package com.BackEndTeam1.entity;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Integer chatId;

    @Column(name = "room_name")
    private String roomName;

    @Enumerated(EnumType.STRING)
    private ChatType dtype; // Enum: channel, dm
}