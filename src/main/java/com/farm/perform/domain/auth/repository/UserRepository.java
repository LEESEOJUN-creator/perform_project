package com.farm.perform.domain.auth.repository;

import com.farm.perform.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsernameAndDeletedFalse(String username);
    Optional<User> findByUserIdAndDeletedFalse(Long userId);
    Optional<User> findByKakaoId(Long kakaoId);

}
