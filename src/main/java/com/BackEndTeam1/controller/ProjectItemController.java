package com.BackEndTeam1.controller;

import com.BackEndTeam1.dto.ProjectItemDTO;
import com.BackEndTeam1.entity.ProjectItem;
import com.BackEndTeam1.service.ProjectItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/project/ProjectItem")
public class ProjectItemController {

    private final ProjectItemService projectItemService;
    private final ModelMapper modelMapper;

    @PostMapping("/create")
    public void createItemGroup(@RequestBody ProjectItem projectItem) {
        projectItemService.createItem(modelMapper.map(projectItem,ProjectItemDTO.class));
    }

    @PutMapping("/update/{no}")
    public ResponseEntity<?> updateItemGroup(@PathVariable Long no, @RequestBody ProjectItemDTO projectItemDTO) {
        try {
            ProjectItem updatedItem = projectItemService.updateItem(no, projectItemDTO);
            return ResponseEntity.ok(updatedItem); // 업데이트된 데이터를 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("업데이트 실패: " + e.getMessage());
        }
    }


    @DeleteMapping("/delete/{no}")
    public ResponseEntity<String> deleteItemGroup(@PathVariable Long no) {
        log.info("삭제번호 : " + no);
        try {
            projectItemService.deleteById(no);
            return ResponseEntity.ok("삭제 성공");
        } catch (Exception e) {
            log.error("삭제 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("삭제 실패: " + e.getMessage());
        }
    }

}
