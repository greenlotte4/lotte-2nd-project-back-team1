package com.BackEndTeam1.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
public class DomainContorller {
    @GetMapping("/domain/check")
    public ResponseEntity checkDomain() {
        //도메인이 정상작동하는지 체크하는 메서드 - 삭제하지 말것
        return ResponseEntity.ok().build();
    }
}
