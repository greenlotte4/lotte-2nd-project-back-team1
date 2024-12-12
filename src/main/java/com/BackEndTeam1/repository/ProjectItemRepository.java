package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.Project;
import com.BackEndTeam1.entity.ProjectItem;
import com.BackEndTeam1.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ProjectItemRepository extends JpaRepository<ProjectItem, Long> {

    ProjectItem findByProjectItemId(Long id);

    ProjectItem deleteByProjectItemId(Long id);

    List<ProjectItem> findByProject_ProjectId(Long projectId);

    List<ProjectItem> findByProject(Project project);
}
