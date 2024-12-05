package com.BackEndTeam1.dto;

import com.BackEndTeam1.entity.Chat;
import com.BackEndTeam1.entity.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChannelDTO {
    private Integer channelId;

    //private Chat chat;

    private String manager;

    private String name;
}
