package com.BackEndTeam1.controller;

import com.BackEndTeam1.dto.CalendarDTO;
import com.BackEndTeam1.dto.CalendarEventDTO;
import com.BackEndTeam1.repository.UserRepository;
import com.BackEndTeam1.service.CalendarEventService;
import com.BackEndTeam1.service.CalendarService;
import com.BackEndTeam1.service.CalendarUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

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
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    //달력 생성
    @PostMapping("/makecalendar")
    public ResponseEntity<CalendarDTO> createCalendar(@RequestBody CalendarDTO calendarDTO) {
        log.info("calendarDTO : "+calendarDTO.toString());
        CalendarDTO createdCalendar = calendarService.createCalendar(calendarDTO);
        return ResponseEntity.ok(createdCalendar);
    }
    //초대 코드로 달력 추가
    @PostMapping("/invitecalendar")
    public ResponseEntity<?> inviteCalendar(@RequestBody Map<String, String> request) {
        String calendarCode = request.get("inviteCode");
        String userId = request.get("userId");
        log.info("request.toString()" + request.toString());
        log.info("calendarCode : "+calendarCode);
        log.info("userId : "+userId);
        try {
            calendarService.handleInvite(calendarCode, userId);
            return ResponseEntity.ok("캘린더 가입 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }
    //달력 리스트 출력
    @PostMapping("/calendarlist")
    public ResponseEntity<List<CalendarDTO>> getCalendarsByUser(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        List<CalendarDTO> calendars = calendarService.getCalendarsByUserId(userId);
        log.info("calendars.toString())"+calendars.toString());
        return ResponseEntity.ok(calendars);
    }
    //달력 삭제
    @DeleteMapping("/deletecalendar")
    public ResponseEntity<?> deleteCalendar(@RequestBody Map<String, Integer> request) {
        Integer calendarId = request.get("calendarId");
        log.info("calendarId : "+calendarId);
        try {
            calendarService.deleteCalendar(calendarId);
            return ResponseEntity.ok("캘린더 삭제 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("캘린더 삭제 실패: " + e.getMessage());
        }
    }
    // 달력 나가기
    @DeleteMapping("/leavecalendar")
    public ResponseEntity<?> leaveCalendar(@RequestBody Map<String, Object> request) {
        Integer calendarId = (Integer) request.get("calendarId");
        String userId = (String) request.get("userId");

        log.info("Received calendarId: "+ calendarId);
        log.info("Received userId: "+ userId);
        try {
            calendarUserService.deleteuserfromcalendar(calendarId, userId);
            return ResponseEntity.ok("팀 나가기 성공");
        } catch (Exception e) {
            log.error("Unexpected error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    //일정 추가하가
    @PostMapping("/addevent")
    public ResponseEntity<?> addevent(@RequestBody List<CalendarEventDTO> eventList) {
        log.info("받은 이벤트 리스트 사이즈: " + eventList.size());
        for (CalendarEventDTO dto : eventList) {
            log.info("이벤트: " + dto.toString());
        }
        // DB에 일정 저장
        calendarEventService.addCalendarEvents(eventList);
        return ResponseEntity.ok("일정 추가 및 전파 성공");
    }
    @MessageMapping("/addevent/send")
    public void addeventsend(@RequestBody List<CalendarEventDTO> eventList) {
        log.info("eventList.toString()"+eventList.size());
        for (CalendarEventDTO event : eventList) {
            try {
                String destination = "/sub/calendar/"  + event.getCalendarId();
                log.info("메시지 전송 경로: {}", destination);
                List<CalendarEventDTO> events = calendarEventService.getEventsByCalendarId(event.getCalendarId());
                log.info("데이터 전달 :"+events);
                messagingTemplate.convertAndSend(destination, events);
            } catch (Exception e) {
                log.error("메시지 처리 중 오류 발생: ", e);
            }
        }
    }
    @PutMapping("/editevent")
    public ResponseEntity<?> editevent(@RequestBody List<CalendarEventDTO> eventList) {
        log.info("받은 수정 이벤트 리스트 사이즈: " + eventList.size());
        for (CalendarEventDTO dto : eventList) {
            log.info("이벤트: " + dto.toString());
        }
        calendarEventService.editCalendarEvents(eventList);
        // 여기서 서비스 호출 로직 등 처리
        return ResponseEntity.ok("일정 추가 성공");
    }
    @DeleteMapping("/deleteevent/{calendarEventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable("calendarEventId") Integer calendarEventId) {
        log.info("calendarEventId : " + calendarEventId);
        calendarEventService.deleteCalendarEvents(calendarEventId);
        return ResponseEntity.ok("일정 삭제 성공");
    }
}
