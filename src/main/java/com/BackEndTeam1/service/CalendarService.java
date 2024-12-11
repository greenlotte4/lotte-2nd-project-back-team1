package com.BackEndTeam1.service;

import com.BackEndTeam1.dto.CalendarDTO;
import com.BackEndTeam1.dto.CalendarEventDTO;
import com.BackEndTeam1.dto.UserDTO;
import com.BackEndTeam1.entity.Calendar;
import com.BackEndTeam1.entity.CalendarEvent;
import com.BackEndTeam1.entity.CalendarUser;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.CalendarEventRepository;
import com.BackEndTeam1.repository.CalendarRepository;
import com.BackEndTeam1.repository.CalendarUserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CalendarService {
    private final CalendarRepository calendarRepository;
    private final CalendarUserRepository calendarUserRepository;
    private final CalendarEventRepository calendarEventRepository;
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
        User user = calendar.getUser();
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
                    List<CalendarEvent> events = calendarEventRepository.findByCalendar(calendar);

                    // 캘린더 DTO 생성
                    CalendarDTO calendarDTO = modelMapper.map(calendar, CalendarDTO.class);

                    // 이벤트 리스트 추가
                    List<CalendarEventDTO> eventDTOs = events.stream()
                            .map(event -> modelMapper.map(event, CalendarEventDTO.class))
                            .collect(Collectors.toList());
                    calendarDTO.setEvents(eventDTOs);

                    return calendarDTO;
                })
                .collect(Collectors.toList());
    }
}
