package com.BackEndTeam1.controller;

import com.BackEndTeam1.dto.ProjectItemDTO;
import com.BackEndTeam1.entity.ProjectItem;
import com.BackEndTeam1.service.ProjectItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
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
    public void updateItemGroup(@PathVariable Long no , @RequestBody ProjectItem projectItem) {
        projectItemService.updateItem(no, modelMapper.map(projectItem,ProjectItemDTO.class));
    }

    @DeleteMapping("/delete/{no}")
    public void deleteItemGroup(@PathVariable Long no){
        projectItemService.deleteById(no);
    }

}
