package com.example.food_delivery_managing_system.Restaurant.dto;

import com.example.food_delivery_managing_system.Restaurant.Restaurant;
import lombok.Getter;
import org.springframework.data.geo.Point;

@Getter
public class RestaurantDetailResponse {
    private Long id;
    private String name;
    private String roadAddress;
    private String detailAddress;
    private Point coordinates;
    private String openAt;
    private String closeAt;
    private String imageUrl;
    private String additionalInfo;
    private Float restaurantRating;
    private Long userId;
    // private Long userId;
    private int likesCount;
    private boolean liked;

    public RestaurantDetailResponse(Restaurant restaurant, boolean liked) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.roadAddress = restaurant.getRoadAddress();
        this.detailAddress = restaurant.getDetailAddress();
        this.coordinates = new Point(restaurant.getCoordinates());
        this.openAt = restaurant.getOpenAt();
        this.closeAt = restaurant.getCloseAt();
        this.imageUrl = restaurant.getImageUrl();
        this.additionalInfo = restaurant.getAdditionalInfo();
        this.restaurantRating = restaurant.getRestaurantRating();
        this.userId = restaurant.getUserId();
        this.likesCount = restaurant.getLikes().size();
        this.liked = liked;
    }
}
