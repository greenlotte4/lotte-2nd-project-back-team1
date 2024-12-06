package com.BackEndTeam1.service;

import com.BackEndTeam1.dto.PlanHistoryDTO;
import com.BackEndTeam1.dto.UserDTO;
import com.BackEndTeam1.entity.Plan;
import com.BackEndTeam1.entity.PlanHistory;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.PlanHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;

@Log4j2
@Service
@RequiredArgsConstructor
public class PlanHistoryService {
    private final PlanHistoryRepository planHistoryRepository;
    private final ModelMapper modelMapper;
    public void savenewplanhistory(User user) {
        PlanHistoryDTO planHistoryDTO = new PlanHistoryDTO();
        planHistoryDTO.setPlan(user.getPlan());
        planHistoryDTO.setUser(user);
        LocalDate today = LocalDate.now();
        LocalDate oneMonthLater = today.plusMonths(1);
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        planHistoryDTO.setStartDate(today);               // 시작 날짜: 오늘
        planHistoryDTO.setEndDate(oneMonthLater);         // 종료 날짜: 한 달 후
        planHistoryDTO.setCreatedAt(currentTimestamp);    // 생성 날짜: 현재 시간
        planHistoryDTO.setUpdatedAt(currentTimestamp);    // 업데이트 날짜: 현재 시간
        PlanHistory planHistory = modelMapper.map(planHistoryDTO, PlanHistory.class);
        planHistoryRepository.save(planHistory);
    }
}
