package com.example.food_delivery_managing_system.admin.dto;

import com.example.food_delivery_managing_system.user.entity.UserStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserListResponse {
    private Long userId;
    private String email;
    private String restaurantName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;
    private UserStatus userStatus;

    public UserListResponse(
            Long userId,
            String email,
            String restaurantName,
            LocalDateTime createdAt,
            UserStatus userStatus
    ) {
        this.userId = userId;
        this.email = email;
        this.restaurantName = restaurantName;
        this.createdAt = createdAt;
        this.userStatus = userStatus;
    }

}
