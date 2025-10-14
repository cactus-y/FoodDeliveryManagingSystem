package com.example.food_delivery_managing_system.admin.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserStatusResponse {
    private String email;
    private String name;
    private String currentStatus;
    private String message;
}
