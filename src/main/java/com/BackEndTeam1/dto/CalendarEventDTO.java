package com.BackEndTeam1.dto;

import com.BackEndTeam1.entity.Calendar;
import com.BackEndTeam1.entity.User;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalendarEventDTO {
    private Integer calendarEventId;
    private Calendar calendar;
    private User assignee;
    private String name;
    private String content;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean notification;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
