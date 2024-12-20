package com.BackEndTeam1.dto;

import com.BackEndTeam1.entity.Calendar;
import com.BackEndTeam1.entity.User;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalendarEventDTO {
    private Integer calendarEventId;
    private Calendar calendar;
    private Integer calendarId;
    private User assignee;
    private String assigneeId;
    private String name;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean notification;
    private Boolean allDay;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
