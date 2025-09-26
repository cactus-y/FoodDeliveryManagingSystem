package com.example.food_delivery_managing_system.RestaurantLike.dto;

import com.example.food_delivery_managing_system.RestaurantLike.Like;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LikeResponse {
    private Long id;
    private LocalDateTime createdAt;
    private Long restaurantId;
    private Long userId;
    private int likesCount;

    public LikeResponse(Like like) {
        this.id = like.getId();
        this.createdAt = like.getCreatedAt();
        this.restaurantId = like.getRestaurant().getId();
        this.userId = like.getUserId();
        this.likesCount = like.getRestaurant().getLikes().size();
    }
}
