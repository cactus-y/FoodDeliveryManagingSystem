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
    private Point coordinates;
    private String openAt;
    private String closeAt;
    private String imageUrl;
    private String additionalInfo;
    private Float restaurantRating;
    private String ownerUsername;
    private String myUsername;
    private int likesCount;
    private boolean liked;

    public RestaurantDetailResponse(Restaurant restaurant, String myUsername, boolean liked) {
        this.restaurantIdx = restaurant.getRestaurantIdx();
        this.name = restaurant.getName();
        this.roadAddress = restaurant.getRoadAddress();
        this.detailAddress = restaurant.getDetailAddress();
        this.coordinates = restaurant.getCoordinates();
        this.openAt = restaurant.getOpenAt();
        this.closeAt = restaurant.getCloseAt();
        this.imageUrl = restaurant.getImageUrl();
        this.additionalInfo = restaurant.getAdditionalInfo();
        this.restaurantRating = restaurant.getRestaurantRating();
        this.ownerUsername = restaurant.getUser().getEmail();
        this.myUsername = myUsername;
        this.likesCount = restaurant.getLikes().size();
        this.liked = liked;
    }
}
