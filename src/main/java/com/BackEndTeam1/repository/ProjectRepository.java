package com.BackEndTeam1.repository;

import com.BackEndTeam1.dto.ProjectSelectDTO;
import com.BackEndTeam1.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    //  맞는 프로젝트 아이디값의 전체 값을 가져오기
    @Query("SELECT p FROM Project p " +
            "LEFT JOIN FETCH p.projectItems pi " +
            "LEFT JOIN FETCH pi.tasks t " +
            "WHERE p.projectId = :projectId")
    List<Project> findAllByProjectId(@Param("projectId") Long projectId);

    //  로그인 된 사용자 생성한 프로젝트 찾기
    List<Project> findAllByUser_UserId(String userId);
//  로그인한 사용자의 참여된 프로젝트 열기
    @Query("SELECT p FROM Project p LEFT JOIN ProjectUser pu ON p.projectId = pu.project.projectId")
    List<Project> findAllByUserProjectId(String userId);

}
