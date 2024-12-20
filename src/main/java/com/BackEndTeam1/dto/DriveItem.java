package com.BackEndTeam1.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriveItem {
    private String name;  // 폴더나 파일 이름
    private String type;  // "folder" 또는 "file"
    private Long size;    // 파일 크기, 폴더의 경우 0일 수 있음
    private List<DriveItem> children;  // 자식 파일들 (폴더 안에 포함된 파일들)
}
