package com.example.food_delivery_managing_system.admin.repository;

import com.example.food_delivery_managing_system.admin.dto.UserListResponse;
import com.example.food_delivery_managing_system.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AdminRepository extends JpaRepository<User, Long> {
    @Query("SELECT new com.example.food_delivery_managing_system.admin.dto.UserListResponse("
            + "    u.userId, "
            + "    u.email, "
            + "    r.name, "
            + "    u.createdAt, "
            + "    u.userStatus "
            + ") "
            + "FROM User u "
            + "LEFT JOIN Restaurant r ON r.user = u "
            + "WHERE u.userRole = 'OWNER' "
            + "ORDER BY u.createdAt DESC")
    List<UserListResponse> findAllUserListWithRestaurantName();
}