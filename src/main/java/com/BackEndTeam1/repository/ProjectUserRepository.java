package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.Project;
import com.BackEndTeam1.entity.ProjectUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProjectUserRepository extends JpaRepository<ProjectUser, Long> {
    List<ProjectUser> findByProject(Project project);
}
