package com.BackEndTeam1.dto;

import com.BackEndTeam1.entity.User;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalendarDTO {
    private Integer calendarId;
    private UserDTO user;
    private String name; // 캘린더 이름
    private Boolean isTeam; // 개인용인지 팀용인지 구분
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String calendarCode;
    private List<CalendarEventDTO> events;
}
