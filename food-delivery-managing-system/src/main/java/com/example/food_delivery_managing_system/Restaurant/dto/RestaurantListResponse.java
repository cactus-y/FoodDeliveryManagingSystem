package com.example.food_delivery_managing_system.Restaurant.dto;

import com.example.food_delivery_managing_system.Restaurant.Restaurant;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RestaurantListResponse {
    private Long id;
    private String name;
    // private Point coordinates;
    private LocalDateTime createdAt;
    private String openAt;
    private String closeAt;
    private String imageUrl;
    private Float restaurantRating;

    public RestaurantListResponse(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        // this.coordinates = restaurant.getCoordinates();
        this.createdAt = restaurant.getCreatedAt();
        this.openAt = restaurant.getOpenAt();
        this.closeAt = restaurant.getCloseAt();
        this.imageUrl = restaurant.getImageUrl();
        this.restaurantRating = restaurant.getRestaurantRating();
    }
}
