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
import java.time.LocalDate;
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
    public ResponseEntity<String> savePlanHistoryAndUpdateUser(@RequestBody PlanHistoryDTO planHistoryDTO) {
        log.info("Received PlanHistoryDTO: " + planHistoryDTO);

        LocalDate startDate = planHistoryDTO.getStartDate();
        LocalDate endDate = planHistoryDTO.getEndDate();

        log.info("Start Date: " + startDate);
        log.info("End Date: " + endDate);
        log.info("planHistoryDTO.getUserId()"+planHistoryDTO.getUserId());
        log.info("planHistoryDTO.getPlanId()"+planHistoryDTO.getPlanId());

        // Plan과 User 객체를 ID로부터 조회
        Optional<Plan> planOptional = planRepository.findById(planHistoryDTO.getPlanId());
        Optional<User> userOptional = userRepository.findByUserId(planHistoryDTO.getUserId());

        log.info("plan: " +planOptional.get());
        log.info("user: " +userOptional.get());

        if (planOptional.isEmpty() || userOptional.isEmpty()) {
            log.error("Plan or User not found for the given IDs.");
            return ResponseEntity.badRequest().body("Invalid plan or user ID.");
        }

        Plan plan = planOptional.get();
        User user = userOptional.get();

        log.info("plan"+plan.toString());
        log.info("user"+user.toString());
        // PlanHistory 객체 생성 및 저장
        PlanHistory planHistory = PlanHistory.builder()
                .user(user)
                .plan(plan)
                .startDate(startDate)
                .endDate(endDate)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .build();

        // User의 plan 업데이트
        user.setPlan(planOptional.get());

        try {
            // PlanHistory와 User 저장
            planHistoryRepository.save(planHistory);
            userRepository.save(user);
        } catch (Exception e) {
            log.error("PlanHistory 저장 또는 User 업데이트 중 오류 발생", e);
            return ResponseEntity.status(500).body("Plan 이력 저장 또는 사용자 업데이트 중 오류가 발생했습니다.");
        }

        return ResponseEntity.ok("Plan 이력이 성공적으로 저장되고, 사용자 정보가 업데이트되었습니다!");
    }
}
