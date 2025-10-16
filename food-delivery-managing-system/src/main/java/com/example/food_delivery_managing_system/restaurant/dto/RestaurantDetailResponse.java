package com.example.food_delivery_managing_system.restaurant.dto;

import com.example.food_delivery_managing_system.restaurant.Restaurant;
import lombok.Getter;
import org.locationtech.jts.geom.Point;

@Getter
public class RestaurantDetailResponse {
    private Long restaurantIdx;
    private String name;
    private String roadAddress;
    private String detailAddress;
    private double longitude;
    private double latitude;
    private String openAt;
    private String closeAt;
    private String imageUrl;
    private String additionalInfo;
    private Float restaurantRating;
    private String ownerUsername;
    private boolean isMyRestaurant;
    private int likesCount;
    private boolean liked;

    public RestaurantDetailResponse(Restaurant restaurant, String myUsername, boolean liked) {
        this.restaurantIdx = restaurant.getRestaurantIdx();
        this.name = restaurant.getName();
        this.roadAddress = restaurant.getRoadAddress();
        this.detailAddress = restaurant.getDetailAddress();
        this.longitude = restaurant.getCoordinates().getX();
        this.latitude = restaurant.getCoordinates().getY();
        this.openAt = restaurant.getOpenAt();
        this.closeAt = restaurant.getCloseAt();
        this.imageUrl = restaurant.getImageUrl();
        this.additionalInfo = restaurant.getAdditionalInfo();
        this.restaurantRating = restaurant.getRestaurantRating();
        this.ownerUsername = restaurant.getUser().getEmail();
        this.isMyRestaurant = restaurant.getUser().getEmail().equals(myUsername);
        this.likesCount = restaurant.getLikes().size();
        this.liked = liked;
    }
}