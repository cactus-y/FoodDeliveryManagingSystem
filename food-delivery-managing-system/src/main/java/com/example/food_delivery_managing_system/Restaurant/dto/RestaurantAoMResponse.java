package com.example.food_delivery_managing_system.Restaurant.dto;

import com.example.food_delivery_managing_system.Restaurant.Restaurant;
import lombok.Getter;
import org.springframework.data.geo.Point;

@Getter
public class RestaurantAoMResponse {
    private Long id;
    private String name;
    private String roadAddress;
    private String detailAddress;
    private Point coordinates;
    private String openAt;
    private String closeAt;
    private String imageUrl;
    private String additionalInfo;

    public RestaurantAoMResponse() {}

    public RestaurantAoMResponse(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.roadAddress = restaurant.getRoadAddress();
        this.detailAddress = restaurant.getDetailAddress();
        this.coordinates = new Point(restaurant.getCoordinates());
        this.openAt = restaurant.getOpenAt();
        this.closeAt = restaurant.getCloseAt();
        this.imageUrl = restaurant.getImageUrl();
        this.additionalInfo = restaurant.getAdditionalInfo();
    }
}
