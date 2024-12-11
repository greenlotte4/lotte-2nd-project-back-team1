package com.BackEndTeam1.dto;

import com.BackEndTeam1.entity.Calendar;
import com.BackEndTeam1.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalendarUserDTO {
    private Integer calendarUserId;
    private Calendar calendar;
    private User user;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
