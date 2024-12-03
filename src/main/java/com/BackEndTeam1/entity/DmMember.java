package com.BackEndTeam1.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dm_member")
public class DmMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dm_member_id")
    private Integer dmMemberId;

    @ManyToOne
    @JoinColumn(name = "dm_id")
    private Dm dm;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private User member;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
