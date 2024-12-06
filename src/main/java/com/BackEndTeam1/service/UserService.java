package com.BackEndTeam1.service;

import com.BackEndTeam1.dto.PageRequestDTO;
import com.BackEndTeam1.dto.PageResponseDTO;
import com.BackEndTeam1.dto.PlanHistoryDTO;
import com.BackEndTeam1.dto.UserDTO;
import com.BackEndTeam1.entity.Plan;
import com.BackEndTeam1.entity.PlanHistory;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.PlanHistoryRepository;
import com.BackEndTeam1.repository.PlanRepository;
import com.BackEndTeam1.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final PlanHistoryService planHistoryService;
    private final PlanRepository planRepository;
    private final PlanHistoryRepository planHistoryRepository;

    public UserDTO saveUser(UserDTO userDTO){
        User user = modelMapper.map(userDTO, User.class);
        //패스워드 암호화 할때 config(RootConfig)에 직접 매핑해줘야함
        user.setPass(passwordEncoder.encode(user.getPass()));
        User savedUser = userRepository.save(user);

        planHistoryService.savenewplanhistory(savedUser);
        return modelMapper.map(savedUser, UserDTO.class);
    }

    public UserDTO updateUser(UserDTO userDTO){
        User user = modelMapper.map(userDTO, User.class);
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDTO.class);
    }

    public UserDTO findByUserId(String userId) {
        Optional<User> user = userRepository.findByUserId(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        log.info("user.get().getCreatedAt()" + user.get().getCreatedAt());
        return modelMapper.map(user.get(), UserDTO.class);
    }

    public  String findUserIdByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return user != null ? user.getUserId() : null;
    }

    public boolean isUserIdAvailable(String userId) {
        Optional<User> existingUser = userRepository.findByUserId(userId);
        log.info("Found user: " + existingUser.isPresent()); // 로그 추가
        return !existingUser.isPresent();
    }

    public boolean isPhoneNumberExists(String phoneNumber) {
        return userRepository.existsByHp(phoneNumber);
    }

    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    public User getLoggedInUser() {
        // Spring Security에서 현재 인증된 사용자 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }

        String username = authentication.getName(); // 인증된 유저의 username(email, id 등)
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public void updateLastLoginTime(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            userRepository.save(user);
        }
    }
    
    //사용자 정보를 리스트 형식 + 페이지 처리 해서 출력
    public PageResponseDTO<UserDTO> userlist(PageRequestDTO pageRequestDTO) {
        log.info("pageRequestDTO : " + pageRequestDTO);
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPg() -1,
                pageRequestDTO.getSize(),
                Sort.by("userId").descending());

        log.info("pageable : " + pageable);

        // 1. User 데이터 페이징 처리
        Page<User> userPage = userRepository.findAll(pageable);
        // 2. 사용자 ID 리스트 추출
        List<String> userIds = userPage.getContent().stream()
                .map(User::getUserId)
                .toList();
        // 3. 사용자 ID로 PlanHistory 로드
        List<PlanHistory> planHistories = planHistoryRepository.findPlanHistoriesByUserIds(userIds);
        // 4. PlanHistory를 사용자별로 그룹화
        Map<String, PlanHistory> planHistoryMap = planHistories.stream()
                .collect(Collectors.toMap(ph -> ph.getUser().getUserId(), ph -> ph));
// 5. User와 PlanHistory 결합하여 UserDTO 생성
        List<UserDTO> userDTOList = userPage.getContent().stream()
                .map(user -> {
                    UserDTO userDTO = modelMapper.map(user, UserDTO.class);

                    // PlanHistory -> PlanHistoryDTO 매핑
                    PlanHistory planHistory = planHistoryMap.get(user.getUserId());
                    if (planHistory != null) {
                        PlanHistoryDTO planHistoryDTO = modelMapper.map(planHistory, PlanHistoryDTO.class);
                        planHistoryDTO.setPlan(planHistory.getPlan());
                        userDTO.setPlanHistory(planHistoryDTO);
                    }

                    return userDTO;
                })
                .toList();
        int total = (int) userPage.getTotalElements();
        PageResponseDTO<UserDTO> responseDTO = PageResponseDTO.<UserDTO>builder()
                .dtoList(userDTOList)
                .pageRequestDTO(pageRequestDTO)
                .total(total)
                .build();
        return responseDTO;

    }

    public void deleteUsers(List<String> userIds) {
        log.info("userIds : " + userIds);
        try {
            for (String userId : userIds) {
                planHistoryRepository.deleteByUserId(userId);
            }
            // 삭제할 사용자 조회
            List<User> usersToDelete = userRepository.findAllById(userIds);

            if (usersToDelete.isEmpty()) {
                throw new IllegalArgumentException("삭제할 사용자가 존재하지 않습니다.");
            }

            // 사용자 삭제
            userRepository.deleteAll(usersToDelete);

        } catch (Exception e) {
            throw new RuntimeException("사용자 삭제 중 오류 발생: " + e.getMessage());
        }
    }

    public void updateUsers(List<Map<String, Object>> userUpdates) {
        for (Map<String, Object> update : userUpdates) {
            log.info("start");
            String userId = (String) update.get("userId");
            log.info("userId :" + userId);
            String role = (String) update.get("role");
            log.info("role :" + role);
            String status = (String) update.get("status");
            log.info("status :" + status);
            Object planIdObj = update.get("planId");
            Long planId = planIdObj instanceof Number ? ((Number) planIdObj).longValue() : null;
            log.info("planId :" + planId);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
            user.setRole(role);
            user.setStatus(status);
            Plan plan = planRepository.findById(planId)
                    .orElseThrow(() -> new IllegalArgumentException("플랜을 찾을 수 없습니다."));
            user.setPlan(plan);
            PlanHistory planHistory = planHistoryRepository.findByUserId(userId);
            planHistory.setPlan(plan);
            planHistoryRepository.save(planHistory);
            userRepository.save(user);
        }
    }

    //페이징 없이 출력
    public List<UserDTO> findAll() {
        List<User> userList = userRepository.findAll();
        List<UserDTO> userDTOList = new ArrayList<>();
        for (User user : userList) {
            userDTOList.add(modelMapper.map(user, UserDTO.class));

        }
        return userDTOList;
    }
}
