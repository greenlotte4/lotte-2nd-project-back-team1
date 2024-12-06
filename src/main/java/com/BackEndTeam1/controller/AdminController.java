package com.BackEndTeam1.controller;

import com.BackEndTeam1.dto.PageRequestDTO;
import com.BackEndTeam1.dto.PageResponseDTO;
import com.BackEndTeam1.dto.UserDTO;
import com.BackEndTeam1.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @GetMapping("/admin/userlist/{pg}")
    public PageResponseDTO<UserDTO> getUserList(PageRequestDTO pageRequestDTO) {
        PageResponseDTO<UserDTO> pageResponseDTO = userService.userlist(pageRequestDTO);
        log.info("pageResponseDTO : "+pageResponseDTO);
        return pageResponseDTO;
    }
    @DeleteMapping("/admin/userlist")
    public ResponseEntity<String> deleteUserList(@RequestBody List<String> userIds) {
        log.info("userIds1 : " + userIds);
        try {
            userService.deleteUsers(userIds);
            return ResponseEntity.ok("사용자 삭제가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("사용자 삭제 중 오류 발생");
        }
    }
    @PutMapping("/admin/updateuserlist")
    public ResponseEntity<String> updateUsers(@RequestBody List<Map<String, Object>> userUpdates) {
        log.info("userUpdates : " + userUpdates);
        try {
            userService.updateUsers(userUpdates);
            return ResponseEntity.ok("사용자 정보가 수정되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("사용자 정보 수정 중 오류 발생");
        }
    }
}
