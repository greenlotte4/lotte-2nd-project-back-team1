package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.Project;
import com.BackEndTeam1.entity.ProjectItem;
import com.BackEndTeam1.entity.Task;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ProjectItemRepository extends JpaRepository<ProjectItem, Long> {

    ProjectItem findByProjectItemId(Long id);

    @Transactional
    @Modifying
    @Query("DELETE FROM ProjectItem p WHERE p.projectItemId = :id")
    void deleteByProjectItemId(@Param("id") Long id);

    List<ProjectItem> findByProject_ProjectId(Long projectId);

    List<ProjectItem> findByProject(Project project);

    @Query("SELECT p FROM ProjectItem p WHERE p.project.projectId = :projectId ORDER BY p.position ASC")
    List<ProjectItem> findByProjectId(@Param("projectId") Long projectId);
}
