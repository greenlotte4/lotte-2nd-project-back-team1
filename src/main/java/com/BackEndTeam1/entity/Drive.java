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


    // 추가된 필드: 내 드라이브인지, 공유 드라이브인지 구분하는 필드
    @Enumerated(EnumType.STRING)
    @Column(name = "drive_type")
    private DriveType driveType;


    // 드라이브 종류 Enum
    public enum DriveType {
        PERSONAL, // 내 드라이브
        SHARED   // 공유 드라이브
    }
}
