package com.BackEndTeam1.dto;

import com.BackEndTeam1.entity.Channel;
import com.BackEndTeam1.entity.User;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChannelMemberDTO {
    private Integer channelMemberId;

    private Channel channel;

    private User member;

    private Timestamp createdAt;

    private Timestamp updatedAt;
}
