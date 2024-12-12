package com.BackEndTeam1.service;

import com.BackEndTeam1.dto.CalendarEventDTO;
import com.BackEndTeam1.entity.Calendar;
import com.BackEndTeam1.entity.CalendarEvent;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.CalendarEventRepository;
import com.BackEndTeam1.repository.CalendarRepository;
import com.BackEndTeam1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CalendarEventService {
    private final CalendarEventRepository calendarEventRepository;
    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;
    public void addCalendarEvents(List<CalendarEventDTO> eventList) {
        for (CalendarEventDTO dto : eventList) {
            Calendar calendar = calendarRepository.findById(Integer.valueOf(dto.getCalendarId()))
                    .orElseThrow(() -> new RuntimeException("해당 캘린더 없음"));

            User assignee = userRepository.findByUserId(dto.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("해당 사용자 없음"));

            // DTO의 startDate, endDate를 Timestamp로 변환 (format 맞춰서)

            CalendarEvent calendarEvent = CalendarEvent.builder()
                    .calendar(calendar)
                    .assignee(assignee)
                    .name(dto.getName())
                    .content(dto.getContent())
                    .startDate(dto.getStartDate())
                    .endDate(dto.getEndDate())
                    .notification(dto.getNotification())
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .build();

            calendarEventRepository.save(calendarEvent);
        }
    }
}
