package com.BackEndTeam1.repository;

import com.BackEndTeam1.dto.ProjectSelectDTO;
import com.BackEndTeam1.entity.Project;
import com.BackEndTeam1.entity.ProjectUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>{
    @Query("SELECT DISTINCT p FROM Project p " +
            "LEFT JOIN p.ProjectItems pi " +
            "LEFT JOIN pi.tasks t " +
            "WHERE p.projectId = :projectId")
    List<Project> findAllByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT pu FROM ProjectUser pu LEFT JOIN pu.project WHERE pu.user.userId = :userId")
    List<ProjectUser> findAllProjectUser(@Param("userId") String userId);

    //  로그인 된 사용자 생성한 프로젝트 찾기
    @Query("SELECT DISTINCT p FROM Project p WHERE p.user.userId = :userId")
    List<Project> findAllByUser_UserId(@Param("userId") String userId);

//  로그인한 사용자의 참여된 프로젝트 열기
    @Query("SELECT DISTINCT p FROM Project p " +
            "JOIN ProjectUser pu ON p.projectId = pu.project.projectId " +
            "WHERE pu.user.userId = :userId")
    List<Project> findAllByUserProjectId(@Param("userId") String userId);


    @Query("SELECT p FROM Project p WHERE p.user.userId = :userId")
    List<Project> findByUserId(@Param("userId") String userId);

    @Query("SELECT COUNT(p) FROM Project p WHERE p.user.userId = :userId")
    int countByUserId(@Param("userId") String userId);
}
