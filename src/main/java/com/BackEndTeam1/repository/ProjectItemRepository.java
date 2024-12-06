package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.ProjectItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectItemRepository extends JpaRepository<ProjectItem, Long> {

    ProjectItem findByProjectItemId(Long id);

    ProjectItem deleteByProjectItemId(Long id);
}
