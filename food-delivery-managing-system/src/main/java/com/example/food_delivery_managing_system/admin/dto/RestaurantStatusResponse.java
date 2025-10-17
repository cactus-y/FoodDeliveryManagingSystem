package com.example.food_delivery_managing_system.admin.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RestaurantStatusResponse {
    private String email;
    private String restaurantName;
    private String previousStatus;
    private String currentStatus;
    private String message;
}
