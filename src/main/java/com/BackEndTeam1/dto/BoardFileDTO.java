package com.BackEndTeam1.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardFileDTO {

    private Integer boardFileId;       // 파일 ID
    private String fileOriginalName;  // 파일의 원본 이름
    private String fileStoredName;    // 파일의 저장된 이름
    private Integer fileSize;         // 파일 크기
    private String fileType;          // 파일 타입 (예: image/jpeg)
    private String createdAt;         // 생성 시간 (포맷된 문자열)
    private String updatedAt;         // 수정 시간 (포맷된 문자열)
}
