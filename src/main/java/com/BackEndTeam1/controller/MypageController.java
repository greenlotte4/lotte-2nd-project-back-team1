package com.BackEndTeam1.controller;

import com.BackEndTeam1.dto.PlanHistoryDTO;
import com.BackEndTeam1.entity.Plan;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.entity.PlanHistory;
import com.BackEndTeam1.repository.PlanHistoryRepository;
import com.BackEndTeam1.repository.PlanRepository;
import com.BackEndTeam1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user/mypage")
public class MypageController {

    private final PlanHistoryRepository planHistoryRepository;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    @PostMapping("/plan-history")
    public ResponseEntity<String> savePlanHistory(@RequestBody PlanHistoryDTO planHistoryDTO) {
        log.info("Received PlanHistoryDTO: " + planHistoryDTO);

//        // planId와 userId가 null인지 체크
//        if (planHistoryDTO.getPlanId() == null || planHistoryDTO.getUserId() == null) {
//            log.error("Plan ID or User ID is null.");
//            return ResponseEntity.badRequest().body("Plan ID and User ID must not be null.");
//        }

        // Plan과 User 객체를 ID로부터 조회
//        Optional<Plan> planOptional = planRepository.findById(planHistoryDTO.getPlanId());
        Optional<User> userOptional = userRepository.findById(String.valueOf(planHistoryDTO.getUserId()));

//        if (planOptional.isEmpty() || userOptional.isEmpty()) {
//            log.error("Plan or User not found for the given IDs.");
//            return ResponseEntity.badRequest().body("Invalid plan or user ID.");
//        }
//
//        Plan plan = planOptional.get();
        User user = userOptional.get();

        // PlanHistory 객체 생성
        PlanHistory planHistory = PlanHistory.builder()
                .user(user)
//                .plan(plan)
                .startDate(planHistoryDTO.getStartDate())
                .endDate(planHistoryDTO.getEndDate())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .build();

        try {
            planHistoryRepository.save(planHistory);
        } catch (Exception e) {
            log.error("Error saving PlanHistory", e);
            return ResponseEntity.status(500).body("Error saving plan history.");
        }

        return ResponseEntity.ok("Plan history saved successfully!");
    }

}
