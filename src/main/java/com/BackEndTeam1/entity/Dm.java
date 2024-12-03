package com.BackEndTeam1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dm")
public class Dm {
    @Id
    @Column(name = "dm_id")
    private Integer dmId;

    @OneToOne
    @JoinColumn(name = "dm_id", referencedColumnName = "chat_id")
    private Chat chat;
}
