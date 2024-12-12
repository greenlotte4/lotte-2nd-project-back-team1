package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByProjectItem_ProjectItemId(Long projectItemId);
}
