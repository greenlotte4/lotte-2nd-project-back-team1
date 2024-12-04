package com.BackEndTeam1.controller;

import com.BackEndTeam1.dto.PageRequestDTO;
import com.BackEndTeam1.dto.PageResponseDTO;
import com.BackEndTeam1.dto.UserDTO;
import com.BackEndTeam1.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
