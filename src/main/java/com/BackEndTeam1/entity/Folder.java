package com.BackEndTeam1.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "folder")
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "folder_id")
    private Integer folderId;

    @ManyToOne
    @JoinColumn(name = "drive_id")
    private Drive drive;

    @ManyToOne
    @JoinColumn(name = "created_user_id")
    private User createdUser;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    private Folder parentFolder;

    private String name;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    private boolean isShared;

    @OneToMany(mappedBy = "folder", fetch = FetchType.EAGER)
    private List<DriveFile> driveFiles; // 해당 폴더에 속한 파일들

    @Builder.Default
    private String type = "folder";
}
