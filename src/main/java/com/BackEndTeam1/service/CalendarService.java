package com.BackEndTeam1.service;

import com.BackEndTeam1.dto.CalendarDTO;
import com.BackEndTeam1.dto.CalendarEventDTO;
import com.BackEndTeam1.dto.UserDTO;
import com.BackEndTeam1.entity.*;
import com.BackEndTeam1.repository.CalendarEventRepository;
import com.BackEndTeam1.repository.CalendarRepository;
import com.BackEndTeam1.repository.CalendarUserRepository;
import com.BackEndTeam1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class CalendarService {
    private final CalendarRepository calendarRepository;
    private final CalendarUserRepository calendarUserRepository;
    private final CalendarEventRepository calendarEventRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    //달력 생성
    public CalendarDTO createCalendar(CalendarDTO calendarDTO) {
        // DTO를 엔티티로 변환
        Calendar calendar = modelMapper.map(calendarDTO, Calendar.class);

        // 생성 시간 설정 (추가적인 필드는 수동으로 설정 가능)
        calendar.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        if (calendar.getIsTeam()) {
            String uniqueCode = generateUniqueInviteCode();
            calendar.setCalendarCode(uniqueCode);
        }
        User user = userRepository.findByUserId(calendar.getUser().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));
        log.info("user : " + user);
        String userId  = user.getUserId();
        int MaxProject = user.getPlan().getMaxProject();
        log.info("maxCollaborators : " + MaxProject);
        // 사용자별 현재 캘린더 수 조회
        int currentCalendarCount = calendarRepository.countByUser_UserId(userId);
        if (currentCalendarCount == MaxProject) {
            throw new IllegalStateException("캘린더의 최대 개수에 도달했습니다.");
        }
        calendar.setUser(user);
        // 엔티티 저장
        Calendar savedCalendar = calendarRepository.save(calendar);

        CalendarUser calendarUser = CalendarUser.builder()
                .calendar(savedCalendar)
                .user(savedCalendar.getUser())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        calendarUserRepository.save(calendarUser);
        // 저장된 엔티티를 DTO로 변환하여 반환
        return modelMapper.map(savedCalendar, CalendarDTO.class);
    }
    private String generateUniqueInviteCode() {
        String inviteCode;
        boolean exists;

        do {
            inviteCode = generateInviteCode(); // 랜덤 코드 생성
            exists = calendarRepository.existsByCalendarCode(inviteCode); // 중복 여부 확인
        } while (exists); // 중복되면 다시 생성

        return inviteCode;
    }
    // 초대 코드 생성 메서드
    private String generateInviteCode() {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbers = "0123456789";

        // 5글자 알파벳 생성
        String randomLetters = new Random().ints(5, 0, letters.length())
                .mapToObj(i -> String.valueOf(letters.charAt(i)))
                .collect(Collectors.joining());

        // 3글자 숫자 생성
        String randomNumbers = new Random().ints(3, 0, numbers.length())
                .mapToObj(i -> String.valueOf(numbers.charAt(i)))
                .collect(Collectors.joining());

        return randomLetters + randomNumbers;
    }
    //등록된 사용자 캘린더 조회
    public List<CalendarDTO> getCalendarsByUserId(String userId) {
        // 캘린더 유저 조회
        List<CalendarUser> calendarUsers = calendarUserRepository.findByUserUserId(userId);

        // 캘린더와 이벤트 정보를 포함한 DTO 생성
        return calendarUsers.stream()
                .map(calendarUser -> {
                    Calendar calendar = calendarUser.getCalendar();

                    // 이벤트 조회
                    List<CalendarEvent> events = calendarEventRepository.findByCalendar(calendar);

                    // 캘린더 DTO 생성
                    CalendarDTO calendarDTO = CalendarDTO.builder()
                            .calendarId(calendar.getCalendarId())
                            .user(UserDTO.builder()
                                    .userId(calendar.getUser().getUserId())
                                    .username(calendar.getUser().getUsername())
                                    .email(calendar.getUser().getEmail())
                                    .profile(calendar.getUser().getProfile())
                                    .statusMessage(calendar.getUser().getStatusMessage())
                                    .build())
                            .name(calendar.getName())
                            .isTeam(calendar.getIsTeam())
                            .createdAt(calendar.getCreatedAt())
                            .updatedAt(calendar.getUpdatedAt())
                            .calendarCode(calendar.getCalendarCode())
                            .build();

                    // 이벤트 DTO 리스트 생성
                    List<CalendarEventDTO> eventDTOs = events.stream()
                            .map(event -> CalendarEventDTO.builder()
                                    .calendarEventId(event.getCalendarEventId())
                                    .calendar(null) // 순환 참조 방지를 위해 null로 설정
                                    .calendarId(event.getCalendar().getCalendarId())
                                    .assigneeId(event.getAssignee() != null ? event.getAssignee().getUserId() : null)
                                    .name(event.getName())
                                    .content(event.getContent())
                                    .startDate(event.getStartDate())
                                    .endDate(event.getEndDate())
                                    .notification(event.getNotification())
                                    .allDay(event.getAllDay())
                                    .createdAt(event.getCreatedAt())
                                    .updatedAt(event.getUpdatedAt())
                                    .build())
                            .collect(Collectors.toList());

                    // 이벤트 리스트를 캘린더 DTO에 추가
                    calendarDTO.setEvents(eventDTOs);

                    return calendarDTO;
                })
                .collect(Collectors.toList());
    }

    public void deleteCalendar(Integer calendarId) {
        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 캘린더입니다."));
        log.info(calendar.getCalendarId());
        // 달력 이벤트 삭제
        calendarEventRepository.deleteByCalendarId(calendar.getCalendarId());
        // 달력 사용자 삭제
        calendarUserRepository.deleteByCalendarId(calendar.getCalendarId());
        calendarRepository.delete(calendar);
    }

    public void handleInvite(String calendarCode, String userId) {
        // calendarCode로 캘린더 조회
        Calendar calendar = (Calendar) calendarRepository.findByCalendarCode(calendarCode)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 캘린더 코드입니다."));
        // 현재 협력자 수 가져오기
        int currentCollaborators = calendarUserRepository.countByCalendar_CalendarId(calendar.getCalendarId());
        log.info("currentCollaborators : " + currentCollaborators);
        // 최대 협력자 수 가져오기
        User user = userRepository.findByUserId(calendar.getUser().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));
        int maxCollaborators = user.getPlan().getMaxCollaborators();
        log.info("maxCollaborators : " + maxCollaborators);
        // 사용자별 현재 캘린더 수 조회
        if (currentCollaborators == maxCollaborators) {
            log.info("ddddd");
            throw new IllegalStateException("협력자 수가 최대치에 도달했습니다.");
        }
        // CalendarUser 중복 확인
        if (calendarUserRepository.existsByCalendar_CalendarIdAndUser_UserId(calendar.getCalendarId(), userId)) {
            throw new IllegalStateException("이미 해당 캘린더에 가입되어 있습니다.");
        }

        // 사용자 엔티티 조회
        User inviteUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));

        // CalendarUser 추가
        CalendarUser calendarUser = CalendarUser.builder()
                .calendar(calendar)
                .user(inviteUser)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        calendarUserRepository.save(calendarUser);
    }
}
