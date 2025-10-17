package com.example.food_delivery_managing_system.admin.dto;

import com.example.food_delivery_managing_system.user.entity.UserStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class UserListResponse {
    private Long userId;
    private String email;
    private List<String> restaurantNames;  // String → List<String>
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;
    private UserStatus userStatus;

    public UserListResponse(
            Long userId,
            String email,
            List<String> restaurantNames,
            LocalDateTime createdAt,
            UserStatus userStatus
    ) {
        this.userId = userId;
        this.email = email;
        this.restaurantNames = restaurantNames;
        this.createdAt = createdAt;
        this.userStatus = userStatus;
    }
}