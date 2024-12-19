package com.BackEndTeam1.service;

import com.BackEndTeam1.dto.CalendarEventDTO;
import com.BackEndTeam1.entity.Calendar;
import com.BackEndTeam1.entity.CalendarEvent;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.CalendarEventRepository;
import com.BackEndTeam1.repository.CalendarRepository;
import com.BackEndTeam1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CalendarEventService {
    private final CalendarEventRepository calendarEventRepository;
    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
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
                    .allDay(dto.getAllDay())
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            calendarEventRepository.save(calendarEvent);
        }
    }

    public void editCalendarEvents(List<CalendarEventDTO> eventList) {
        for (CalendarEventDTO dto : eventList) {
            // 1. 이벤트 ID로 기존 이벤트 조회
            CalendarEvent existingEvent = calendarEventRepository.findById(dto.getCalendarEventId())
                    .orElseThrow(() -> new RuntimeException("해당 이벤트 없음"));

            // 2. DTO 데이터로 엔티티 업데이트
            existingEvent.setName(dto.getName());//제목
            existingEvent.setContent(dto.getContent());//내용
            existingEvent.setStartDate(dto.getStartDate());//시작날짜
            existingEvent.setEndDate(dto.getEndDate());//종료 날짜
            existingEvent.setAllDay(dto.getAllDay());//하루종일 여부
            // 5. 업데이트된 이벤트 저장
            calendarEventRepository.save(existingEvent);
        }
    }

    public void deleteCalendarEvents(Integer calendarEventId) {
        calendarEventRepository.deleteById(calendarEventId);
    }

    public List<CalendarEventDTO> getEventsByCalendarId(Integer calendarId) {
        List<CalendarEvent> calendarEvents = calendarEventRepository.findByCalendar_CalendarId((calendarId));
        return calendarEvents.stream()
                .map(event -> {
                    CalendarEventDTO dto = modelMapper.map(event, CalendarEventDTO.class);
                    dto.setCalendarId((event.getCalendar().getCalendarId())); // `calendarId` 매핑
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
