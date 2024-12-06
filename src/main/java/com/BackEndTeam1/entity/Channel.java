package com.BackEndTeam1.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "channel")
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "channel_id")
    private Integer channelId;

    @OneToOne
    @JoinColumn(name = "channel_id", referencedColumnName = "chat_id")
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User manager;

    private String name;
}

