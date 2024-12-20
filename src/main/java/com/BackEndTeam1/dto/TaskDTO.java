package com.BackEndTeam1.dto;


import com.BackEndTeam1.entity.ProjectItem;
import com.BackEndTeam1.entity.Task;
import lombok.*;
import java.sql.Date;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    private Long taskId;
    private Long groupId; // groupId를 직접 추가
    private ProjectItem projectItem; // 기존 projectItem 필드 유지
    private String assignee;
    private String name;
    private String description;
    private Integer priority;
    private String status;
    private Date startDate;
    private Date endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // groupId가 null일 경우 projectItem에서 가져옴 (옵션)
    public Long getGroupId() {
        return this.groupId != null ? this.groupId : (this.projectItem != null ? this.projectItem.getProjectItemId() : null);
    }

    public TaskDTO(Task task) {
        this.taskId = task.getTaskId();
        this.name = task.getName();
        this.status = task.getStatus();
        this.groupId = getGroupId();
        this.assignee = task.getAssignee() != null ? task.getAssignee().getUserId() : "Unassigned";
        this.priority = task.getPriority();
        this.startDate = task.getStartDate();
        this.endDate = task.getEndDate();
        this.projectItem = task.getProjectItem();
    }

    public TaskDTO(String name, Date startDate, Date endDate, Integer priority, String s) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.priority = priority;
        this.status = s;
    }
}
