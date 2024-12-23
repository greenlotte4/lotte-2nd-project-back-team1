package com.BackEndTeam1.dto;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FolderRequestDTO {
    private String folderName; // 폴더 이름
    private String userId;     // 생성자의 userId
    private boolean isShared;  // 공유 여부
    private Integer folderId;
    private Integer parentFolderId; // 부모 폴더 ID (새로운 폴더가 부모 폴더를 가질 수 있도록)


}
