package com.BackEndTeam1.repository;

import com.BackEndTeam1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserId(String UserId);

    User findUserChangeByUserId(String userId);

    Optional<User> findByUsername(String username);
    User findByEmail(String email);
    Optional<User> findByUserIdAndEmail(String userId, String email);

    // 맞는 아이디 확인
    boolean existsByUserId(String UserId);


    //맞는 전화번호 확인
    boolean existsByHp(String phoneNumber);

    // 맞는 이메일 확인
    boolean existsByEmail(String email);



}
