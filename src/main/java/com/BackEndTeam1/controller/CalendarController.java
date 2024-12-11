package com.BackEndTeam1.controller;

import com.BackEndTeam1.dto.CalendarDTO;
import com.BackEndTeam1.service.CalendarEventService;
import com.BackEndTeam1.service.CalendarService;
import com.BackEndTeam1.service.CalendarUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/calendar") // 경로를 명시적으로 설정
public class CalendarController {
    private final CalendarService calendarService;
    private final CalendarUserService calendarUserService;
    private final CalendarEventService calendarEventService;
    //달력 생성
    @PostMapping("/makecalendar")
    public ResponseEntity<CalendarDTO> createCalendar(@RequestBody CalendarDTO calendarDTO) {
        log.info("calendarDTO : "+calendarDTO.toString());
        CalendarDTO createdCalendar = calendarService.createCalendar(calendarDTO);
        return ResponseEntity.ok(createdCalendar);
    }
    //달력 리스트 출력
    @PostMapping("/calendarlist")
    public ResponseEntity<List<CalendarDTO>> getCalendarsByUser(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        List<CalendarDTO> calendars = calendarService.getCalendarsByUserId(userId);
        return ResponseEntity.ok(calendars);
    }
}
