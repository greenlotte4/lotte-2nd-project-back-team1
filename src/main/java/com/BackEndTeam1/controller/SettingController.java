package com.BackEndTeam1.controller;

import com.BackEndTeam1.dto.UserDTO;
import com.BackEndTeam1.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/setting")
public class SettingController {

//  사용자 정보 출력
    private final UserService userService;
    @GetMapping("/user/{id}")
    public UserDTO getUserinfo(@PathVariable("id") String userId) {
        UserDTO userdto = userService.findByUserId(userId);
        return userdto;
    }
}
