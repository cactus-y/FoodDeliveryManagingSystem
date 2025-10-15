package com.example.food_delivery_managing_system.restaurantLike.dto;

import com.example.food_delivery_managing_system.restaurantLike.Like;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LikeResponse {
    private Long likeIdx;
    private LocalDateTime createdAt;
    private Long restaurantId;
    private Long userId;
    private int likesCount;

    public LikeResponse(Like like) {
        this.likeIdx= like.getLikeIdx();
        this.createdAt = like.getCreatedAt();
        this.restaurantId = like.getRestaurant().getRestaurantIdx();
        this.userId = like.getUser().getUserId();
        this.likesCount = like.getRestaurant().getLikes().size();
    }
}
