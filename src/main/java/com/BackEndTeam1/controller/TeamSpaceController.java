package com.BackEndTeam1.controller;

import com.BackEndTeam1.dto.TeamSpaceMemberDTO;
import com.BackEndTeam1.dto.TeamSpaceUsersDto;
import com.BackEndTeam1.dto.UserDTO;
import com.BackEndTeam1.entity.TeamSpace;
import com.BackEndTeam1.entity.TeamSpaceMember;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.service.TeamSpaceMemberService;
import com.BackEndTeam1.service.TeamSpaceService;
import com.BackEndTeam1.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/teamspace")
public class TeamSpaceController {
    private final TeamSpaceService teamSpaceService;
    private final TeamSpaceMemberService teamSpaceMemberService;
    private final UserService userService;

    //방생성
    @PostMapping("/maketeam")
    public ResponseEntity<?> makeTeamSpace(@RequestBody Map<String, Object> teamSpaces) {
        try {
            // 요청 데이터 가져오기
            String roomName = (String) teamSpaces.get("roomName");
            String userId = (String) teamSpaces.get("userId");

            if (roomName == null || roomName.isBlank()) {
                return ResponseEntity.badRequest().body("방 이름은 필수입니다.");
            }

            if (userId == null || userId.isBlank()) {
                return ResponseEntity.badRequest().body("유저 ID는 필수입니다.");
            }

            // 날짜 생성
            LocalDate today = LocalDate.now();
            LocalDate oneMonthLater = today.plusMonths(1);

            // 사용자 조회
            User user = userService.findEntityByUserId(userId);

            // 고유 serialNumber 생성 및 중복 확인
            String serialNumber;
            do {
                serialNumber = generateSerialNumber();
            } while (teamSpaceService.serialNumberExists(serialNumber));

            // TeamSpace 생성
            TeamSpace teamSpace = TeamSpace.builder()
                    .roomname(roomName)
                    .serialnumber(serialNumber)
                    .endDate(oneMonthLater)
                    .user(user)
                    .build();

            // TeamSpace 저장
            teamSpaceService.save(teamSpace);

            // TeamSpaceMember 생성 및 저장
            TeamSpaceMember teamSpaceMember = TeamSpaceMember.builder()
                    .teamSpace(teamSpace)
                    .user(user)
                    .build();
            teamSpaceMemberService.membersave(teamSpaceMember);

            // 성공적으로 생성된 TeamSpace 반환
            return ResponseEntity.ok(teamSpace);

        } catch (IllegalArgumentException e) {
            // 유효하지 않은 사용자 ID 예외 처리
            log.error("유효하지 않은 사용자 ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            // 최대 프로젝트 수 초과 예외 처리
            log.error("최대 프로젝트 수 초과: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            // 기타 예외 처리
            log.error("예기치 않은 오류 발생: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("팀 생성 중 오류가 발생했습니다. 다시 시도해주세요.");
        }
    }


    //초대 번호 생성
    private String generateSerialNumber() {
        Random random = new Random();
        StringBuilder serial = new StringBuilder();

        // 4개의 대문자 추가
        for (int i = 0; i < 4; i++) {
            char uppercase = (char) ('A' + random.nextInt(26));
            serial.append(uppercase);
        }

        // 4개의 숫자 추가
        for (int i = 0; i < 4; i++) {
            char digit = (char) ('0' + random.nextInt(10));
            serial.append(digit);
        }

        return serial.toString();
    }
    //방입장
    @PostMapping("/jointeamroom")
    public ResponseEntity<?> jointeamRoom(@RequestBody Map<String, Object> request) {
        String serialNumber = (String) request.get("serialnumber");
        String userId = (String) request.get("userId");
        log.info("serialNumber : " + serialNumber);
        log.info("userId : " + userId);

        try {
            // 팀 스페이스 찾기
            TeamSpace teamSpace = teamSpaceService.findBySerialNumber(serialNumber);
            if (teamSpace == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Invalid serial number", "serialnumber", serialNumber));
            }

            // 팀 멤버 추가
            boolean isAdded = teamSpaceMemberService.addMemberToTeamSpace(teamSpace.getTeamSpaceId(), userId);
            if (!isAdded) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "User already added to the team", "userId", userId));
            }

            // 성공 응답
            return ResponseEntity.ok(teamSpace);

        } catch (IllegalStateException e) {
            // 최대 협업 인원 초과 예외 처리
            log.error("Max collaborators limit exceeded", e);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage(), "serialnumber", serialNumber));
        } catch (Exception e) {
            // 기타 예외 처리
            log.error("Error joining team room", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred", "details", e.getMessage()));
        }
    }

    //방삭제
    @DeleteMapping("/deleteteam")
    public ResponseEntity<String> deleteTeamSpace(@RequestBody Map<String, Object> TeamSpaces) {
        String roomname = (String) TeamSpaces.get("roomname");
        String userId = (String) TeamSpaces.get("userId");
        TeamSpace teamSpace = teamSpaceService.findByUserAndRoomname(userId, roomname);
        // teamSpaceMemberService에서 teamspaceId와 관련된 데이터 삭제
        teamSpaceMemberService.deleteAllByTeamspaceId(teamSpace.getTeamSpaceId());
        // teamSpace 삭제
        teamSpaceService.delete(teamSpace);

        return ResponseEntity.ok("Deleted : " + roomname);
    }
    //추방 또는 방나가기
    @DeleteMapping("/outteamroom")
    public ResponseEntity<Boolean> outTeamSpace(@RequestBody Map<String, Object> teamSpaces) {
        try {
            Long teamspaceId = ((Number) teamSpaces.get("teamspaceId")).longValue();
            String userId = (String) teamSpaces.get("userId");

            log.info("teamspaceId: " + teamspaceId + ", userId: " + userId);

            // 삭제 처리
            boolean isDeleted = teamSpaceMemberService.deleteByTeamspaceIdAndUserId(teamspaceId, userId);

            return ResponseEntity.ok(isDeleted);
        } catch (Exception e) {
            log.error("방 나가기 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }
    //방수정
    @PutMapping("/updateteamroom")
    public ResponseEntity<String> updateTeamRoom(@RequestBody Map<String, Object> request) {
        String userId = (String) request.get("userId");
        Long teamspaceId = Long.valueOf((String) request.get("teamspaceId"));
        String roomname = (String) request.get("roomname");

        // 방 수정 요청 처리
        boolean isUpdated = teamSpaceService.updateRoomName(userId, teamspaceId, roomname);

        if (!isUpdated) {
            return ResponseEntity.badRequest().body("Failed to update the room. Check your permissions.");
        }

        return ResponseEntity.ok("Room name updated successfully to: " + roomname);
    }

    //방 가지고 오기
    @GetMapping("/getlistteamroom")
    public ResponseEntity<List<TeamSpace>> getTeamSpaceListTeamRoom(@RequestParam String userId) {
        log.info("userId : "+userId);
        try {
            // 사용자 ID로 참여 중인 방 목록 조회
            List<TeamSpace> teamSpaces = teamSpaceService.getTeamSpacesByUserId(userId);
            log.info("teamSpaces.size() : " + teamSpaces.size());
            if (teamSpaces.isEmpty()) {
                return ResponseEntity.noContent().build(); // 참여 중인 방이 없을 경우
            }

            return ResponseEntity.ok(teamSpaces); // 참여 중인 방 목록 반환
        } catch (Exception e) {
            log.error("방 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // 오류 응답
        }
    }
    @PostMapping("/listbyid")
    public ResponseEntity<?> postUserListbyUserId(@RequestBody Map<String, Object> request) {
        String userId = (String) request.get("userId");
        List<TeamSpaceUsersDto> users = teamSpaceMemberService.getUsersInTeamSpacesByUserId(userId);
        return ResponseEntity.ok(users);
    }
}