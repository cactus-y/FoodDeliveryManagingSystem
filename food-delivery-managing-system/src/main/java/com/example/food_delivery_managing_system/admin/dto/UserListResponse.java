package com.example.food_delivery_managing_system.admin.dto;

import com.example.food_delivery_managing_system.user.entity.UserStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserListResponse {
    private Long userId;
    private String email;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;
    private UserStatus userStatus;

    public UserListResponse(
            Long userId,
            String email,
            String name,
            LocalDateTime createdAt,
            UserStatus userStatus
    ) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.createdAt = createdAt;
        this.userStatus = userStatus;
    }

}
