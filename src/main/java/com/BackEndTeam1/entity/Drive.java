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
@Table(name = "dirve")
public class Drive {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "drive_id")
    private Integer driveId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "max_collaborators")
    private Integer maxCollaborators;

    @Column(name = "drive_capacity")
    private Integer driveCapacity;

    @Column(name = "max_file_size")
    private Integer maxFileSize;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
