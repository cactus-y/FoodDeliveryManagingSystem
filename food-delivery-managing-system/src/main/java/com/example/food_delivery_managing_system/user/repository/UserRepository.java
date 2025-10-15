package com.example.food_delivery_managing_system.user.repository;

import com.example.food_delivery_managing_system.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    boolean existsByEmail(String email);
    boolean existsByNickName(String nickName);
    User findByEmail(String email);

}
