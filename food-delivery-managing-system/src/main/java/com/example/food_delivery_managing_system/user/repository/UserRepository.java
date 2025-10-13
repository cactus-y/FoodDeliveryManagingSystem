package com.example.food_delivery_managing_system.user.repository;

import com.example.food_delivery_managing_system.user.eneity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByNickName(String nickName);

    Optional<User> findByEmail(String email); // ★ 추가
}
