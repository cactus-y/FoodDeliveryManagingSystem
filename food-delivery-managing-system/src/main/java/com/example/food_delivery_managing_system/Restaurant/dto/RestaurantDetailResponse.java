package com.example.food_delivery_managing_system.Restaurant.dto;

import com.example.food_delivery_managing_system.Restaurant.Restaurant;
import lombok.Getter;

@Getter
public class RestaurantDetailResponse {
    private Long id;
    private String name;
    private String roadAddress;
    private String detailAddress;
    // private Point coordinates;
    private String openAt;
    private String closeAt;
    private String imageUrl;
    private String additionalInfo;
    private Float restaurantRating;
    // private Long userId;
    private int likesCount;

    public RestaurantDetailResponse(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.roadAddress = restaurant.getRoadAddress();
        this.detailAddress = restaurant.getDetailAddress();
        this.openAt = restaurant.getOpenAt();
        this.closeAt = restaurant.getCloseAt();
        this.imageUrl = restaurant.getImageUrl();
        this.additionalInfo = restaurant.getAdditionalInfo();
        this.restaurantRating = restaurant.getRestaurantRating();
        this.likesCount = restaurant.getLikes().size();
    }
}
