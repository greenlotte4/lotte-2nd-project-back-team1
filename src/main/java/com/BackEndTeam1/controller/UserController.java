package com.BackEndTeam1.controller;

import com.BackEndTeam1.dto.UserDTO;
import com.BackEndTeam1.entity.Plan;
import com.BackEndTeam1.entity.TeamSpace;
import com.BackEndTeam1.entity.TeamSpaceMember;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.jwt.JwtProvider;
import com.BackEndTeam1.repository.PlanRepository;
import com.BackEndTeam1.repository.TeamSpaceMemberRepository;
import com.BackEndTeam1.repository.TeamSpaceRepository;
import com.BackEndTeam1.security.MyUserDetails;
import com.BackEndTeam1.service.DriveFileService;
import com.BackEndTeam1.service.DriveService;
import com.BackEndTeam1.service.FileService;
import com.BackEndTeam1.service.UserService;
import com.BackEndTeam1.service.mongo.UserLoginService;
import com.BackEndTeam1.util.CustomFileUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final FileService fileService;
    private final CustomFileUtil customFileUtil;
    private final TeamSpaceMemberRepository teamSpaceMemberRepository;
    private final TeamSpaceRepository teamSpaceRepository;
    private final UserLoginService userLoginService;
    private final DriveFileService driveFileService;
    private final DriveService driveService;


    // 회원가입
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody UserDTO userDTO) {
        log.info("화원가입 요청");
        Plan plan = new Plan();
        plan.setPlanId(1L);
        userDTO.setPlan(plan);
        userDTO.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        UserDTO savedUser = userService.saveUser(userDTO);

        return ResponseEntity.status(HttpStatus.OK).body(savedUser);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
        log.info("로그인 요청: " + userDTO.getUserId());
        try {
            log.info("로그인 시도: " + userDTO.getUserId());

            // 사용자 인증
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDTO.getUserId(), userDTO.getPass());
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 인증된 사용자 정보 가져오기
            MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            String status = user.getStatus();
            log.info("user: " + user);
            userService.updateLastLoginTime(user.getUserId());

            // 상태 검사
            if ("BANED".equals(status)) {
                log.info("벤됨");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("정지된 회원입니다");
            } else if ("DELETED".equals(status)) {
                log.info("탈퇴됨");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("탈퇴한 회원입니다.");
            } else if ("SLEEP".equals(status)) {
                log.info("휴면 계정");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("휴면 계정입니다.");
            }

            // JWT 토큰 발행
            String accessToken = jwtProvider.createToken(user, 1);
            String refreshToken = jwtProvider.createToken(user, 7);
            log.info("accessToken 생성 완료");

            // TeamSpaceMember 리스트 조회
            List<TeamSpaceMember> teamSpaceMembers  = teamSpaceMemberRepository.findByUser_UserId(user.getUserId());
            List<String> roomNames = teamSpaceMembers.stream()
                    .map(TeamSpaceMember::getTeamSpace) // TeamSpace 추출
                    .map(TeamSpace::getRoomname) // roomname 추출
                    .collect(Collectors.toList()); // roomname 리스트 생성
            List<Long> teamid = teamSpaceMembers.stream()
                    .map(TeamSpaceMember::getTeamSpace) // TeamSpace 추출
                    .map(TeamSpace::getTeamSpaceId) // roomname 추출
                    .collect(Collectors.toList()); // roomname 리스트 생성
            userLoginService.updateUserStatus(user.getUserId(), "online", roomNames,teamid, user.getProfile(), user.getUsername());
            userService.loginStatusChange(user.getUserId(), "online");
            String profile = userService.findProfileUrl(user.getUserId());
            // 리프레쉬 토큰 DB저장
            driveService.initializeDrivesForUser();
            // 토큰 전송
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("username", user.getUsername());
            resultMap.put("userid", user.getUserId());
            resultMap.put("role", user.getRole());
            resultMap.put("email", user.getEmail());
            resultMap.put("accessToken", accessToken);
            resultMap.put("refreshToken", refreshToken);
            resultMap.put("profile", profile);
            log.info("resultMap"+resultMap);
            return ResponseEntity.ok(resultMap);

        }catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("아이디 또는 비밀번호가 다릅니다.");
        }
    }

    @GetMapping("/checkUserId/{userId}")
    public ResponseEntity<Map<String, Boolean>> checkUserId(@PathVariable String userId) {
        log.info("중복확인 요청" + userId);
        boolean isAvailable = userService.isUserIdAvailable(userId);
        return ResponseEntity.ok(Map.of("isAvailable", isAvailable));
    }

    @GetMapping("/list")
    @ResponseBody
    public List<UserDTO> getUserList() {
        List<UserDTO> users = userService.findAll();
//        log.info(users.toString());
        return users;
//        return new ArrayList<>();
    }

    @GetMapping("/thumb/{fileName}")
    public ResponseEntity<Resource> thumbnail(@PathVariable String fileName){

        log.info(fileName);

        return customFileUtil.getFile(fileName);
    }



    @PostMapping("/hpcheck")
    public ResponseEntity<Map<String, Boolean>> checkHp(@RequestBody Map<String, String> requestBody) {
        log.info("휴대폰 중복검사 요청 : " + requestBody);

        // 요청 본문에서 phoneNumber를 추출
        String phoneNumber = requestBody.get("phoneNumber");

        // 전화번호 중복 여부를 서비스에서 확인
        boolean isAvailable = userService.isPhoneNumberExists(phoneNumber);

        // 결과 반환
        return ResponseEntity.ok(Map.of("isAvailable", isAvailable));
    }


    @PostMapping("/findEmail")
    public ResponseEntity<Map<String, String>> findEmail(@RequestBody Map<String, String> requestBody) {
        log.info("아이디 찾기 요청 : " + requestBody);
        String email = requestBody.get("email");

        String userId = userService.findUserIdByEmail(email);
        log.info("유저아이디 : " + userId);
        // 아이디를 찾았다면
        if (userId != null) {
            return ResponseEntity.ok(Map.of("userId", userId));  // 아이디 반환
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "해당 이메일로 등록된 아이디가 없습니다."));  // 아이디를 찾지 못했을 경우}
        }
    }

    @PostMapping("/newPass")
    public ResponseEntity newPass(@RequestBody Map<String, String> requestBody) {
        log.info("비밀번호번경 요청 : " + requestBody);
        String userId = requestBody.get("userId");
        String pass = requestBody.get("newPassword");
        return ResponseEntity.status(HttpStatus.OK).body(userService.changePassword(userId,pass));
    }

    @PutMapping("/newHp")
    public ResponseEntity changeHp(@RequestBody Map<String, String> requestBody) {
        log.info("요청온 번호 : " + requestBody);

        String userId = requestBody.get("userId");
        log.info("유저아이디 : " + userId);
        String hp = requestBody.get("hp");

        return ResponseEntity.status(HttpStatus.OK).body(userService.changeHp(userId, hp));
    }
    @PutMapping("/newEmail")
    public ResponseEntity changeEmail(@RequestBody Map<String, String> requestBody) {
        log.info("요청온 이메일 : " + requestBody);

        String userId = requestBody.get("userId");
        log.info("유저아이디 : " + userId);
        String email = requestBody.get("email");

        return ResponseEntity.status(HttpStatus.OK).body(userService.changeEmail(userId, email));
    }
    @PutMapping("/statusMessage")
    public ResponseEntity changeStatusMessage(@RequestBody Map<String, String> requestBody) {
        log.info("요청온 메시지 : " + requestBody);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("현재 인증 상태: " + authentication);
        String userId = requestBody.get("userId");
        log.info("유저아이디 : " + userId);
        String statusMessage = requestBody.get("statusMessage");

        return ResponseEntity.status(HttpStatus.OK).body(userService.changeStatusMessage(userId, statusMessage));
    }

    @PostMapping("/profile")
    public ResponseEntity<?> changeProfile(@RequestParam("userId") String userId,
                                           @RequestPart("profileImage") MultipartFile file) {
        log.info("요청온 유저아이디: " + userId);

        try {
            // 서비스 호출
            String fileDownloadUri = fileService.uploadProfileImage(userId, file);
            return ResponseEntity.status(HttpStatus.OK).body(fileDownloadUri); // 성공 시 이미지 URL 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("업로드 실패: " + e.getMessage());
        }
    }

    @PutMapping("/delete")
    public ResponseEntity delete(@RequestBody Map<String, String> requestBody) {
        log.info("삭제 요청온 유저아이디: " + requestBody);
        String userId = requestBody.get("userId");
        return ResponseEntity.status(HttpStatus.OK).body(userService.deleteUser(userId));
    }
    @GetMapping("/profileUrl")
    public ResponseEntity<String> profileUrl(@RequestParam String userId) {
        log.info("요청온 유저아이디1: " + userId);

        return ResponseEntity.status(HttpStatus.OK).body(userService.findProfileUrl(userId));
    }

    @PutMapping("/loginStatus")
    public ResponseEntity loginStatus(@RequestBody Map<String, String> requestBody) {
        String userId = requestBody.get("userId");
        String userStatus = requestBody.get("userStatus");
        log.info("유저 상태 변경요청: " + userStatus);
        userLoginService.changeUserStatus(userId, userStatus);
        userService.loginStatusChange(userId, userStatus);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("상태가 변경 되었습니다.");
//        return ResponseEntity.status(HttpStatus.OK).body(userService.loginStatusChange(userId,userStatus));
    }

    @GetMapping("/selectLoginStatus")
    public ResponseEntity selectLoginStatus(@RequestParam String userId) {
        log.info("유저 상태 조회요청: " + userId);
        return ResponseEntity.status(HttpStatus.OK).body(userService.selectStatus(userId));
    }
}
