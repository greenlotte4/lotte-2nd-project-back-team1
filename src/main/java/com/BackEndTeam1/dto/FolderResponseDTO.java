package com.BackEndTeam1.dto;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FolderResponseDTO {
    private Integer folderId;    // 폴더 ID
    private String folderName;   // 폴더 이름
    private String driveType;    // 드라이브 타입 (공유/개인)
    private Timestamp createdAt; // 생성 시간
}
