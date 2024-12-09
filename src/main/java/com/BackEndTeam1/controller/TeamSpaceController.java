package com.BackEndTeam1.controller;

import com.BackEndTeam1.dto.UserDTO;
import com.BackEndTeam1.entity.TeamSpace;
import com.BackEndTeam1.entity.TeamSpaceMember;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.service.TeamSpaceMemberService;
import com.BackEndTeam1.service.TeamSpaceService;
import com.BackEndTeam1.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    @GetMapping("/maketeam")
    public ResponseEntity<String> makeTeamSpace(
            @RequestBody Map<String, Object> TeamSpaces) {
        String roomname = (String) TeamSpaces.get("roomname");
        String userId = (String) TeamSpaces.get("userId");

        LocalDate today = LocalDate.now();
        LocalDate oneMonthLater = today.plusMonths(1);

        User user = userService.findEntityByUserId(userId);

        String serialNumber;

        do {
            serialNumber = generateSerialNumber();
        } while (teamSpaceService.serialNumberExists(serialNumber)); // 중복 확인

        TeamSpace teamSpace = TeamSpace.builder()
                .roomname(roomname)
                .serialnumber(serialNumber)
                .endDate(oneMonthLater)
                .user(user)
                .build();
        log.info("teamSpace.toString() : " + teamSpace.getRoomname());
        teamSpaceService.save(teamSpace); // DB에 저장

        TeamSpaceMember teamSpaceMember = TeamSpaceMember.builder()
                .teamSpace(teamSpace)
                .user(user)
                .build();
        log.info("teamSpaceMember.toString() : " + teamSpaceMember.getUser());
        teamSpaceMemberService.membersave(teamSpaceMember);
        return ResponseEntity.ok("Team space successfully created with serial number: " + serialNumber);
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
    public ResponseEntity<String> jointeamRoom(@RequestBody Map<String, Object> request){
        String serialNumber = (String) request.get("serialNumber");
        String userId = (String) request.get("userId");
        TeamSpace teamSpace = teamSpaceService.findBySerialNumber(serialNumber);
        if (teamSpace == null) {
            return ResponseEntity.badRequest().body("Invalid serial number.");
        }
        // TeamSpaceMember 저장 (중복 확인)
        boolean isAdded = teamSpaceMemberService.addMemberToTeamSpace(teamSpace.getTeamSpaceId(), userId);

        if (!isAdded) {
            return ResponseEntity.badRequest().body("User is already a member of this team space.");
        }

        return ResponseEntity.ok("Successfully joined the team room: " + teamSpace.getRoomname());
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
    public ResponseEntity<String> outTeamSpace(@RequestBody Map<String, Object> TeamSpaces) {
        Long  teamspaceId = Long.valueOf((String) TeamSpaces.get("teamspaceId"));
        String userId = (String) TeamSpaces.get("userId");
        teamSpaceMemberService.deleteByTeamspaceIdAndUserId(teamspaceId, userId);

        return ResponseEntity.ok("User " + userId + " roomnumber: " + teamspaceId);
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

}