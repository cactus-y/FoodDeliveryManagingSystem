package com.example.food_delivery_managing_system.restaurant.dto;

import com.example.food_delivery_managing_system.restaurant.Restaurant;
import com.example.food_delivery_managing_system.user.entity.User;
import lombok.Getter;
import org.locationtech.jts.geom.Point;

@Getter
public class RestaurantAoMResponse {
    private Long restaurantIdx;
    private String name;
    private String roadAddress;
    private String detailAddress;
    private Point coordinates;
    private String openAt;
    private String closeAt;
    private String imageUrl;
    private String additionalInfo;
    private String username;

    public RestaurantAoMResponse(String myUsername) {
        this.username = myUsername;
    }

    public RestaurantAoMResponse(Restaurant restaurant, String myUsername) {
        this.restaurantIdx = restaurant.getRestaurantIdx();
        this.name = restaurant.getName();
        this.roadAddress = restaurant.getRoadAddress();
        this.detailAddress = restaurant.getDetailAddress();
        this.coordinates = restaurant.getCoordinates();
        this.openAt = restaurant.getOpenAt();
        this.closeAt = restaurant.getCloseAt();
        this.imageUrl = restaurant.getImageUrl();
        this.additionalInfo = restaurant.getAdditionalInfo();
        this.username = myUsername;
    }
}
