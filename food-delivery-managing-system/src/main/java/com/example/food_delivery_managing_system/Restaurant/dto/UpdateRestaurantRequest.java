package com.example.food_delivery_managing_system.Restaurant.dto;

import lombok.Getter;
import org.springframework.data.geo.Point;

@Getter
public class UpdateRestaurantRequest {
    private String name;
    private String roadAddress;
    private String detailAddress;
    private Point coordinates;
    private String openAt;
    private String closeAt;
    private String imageUrl;
    private String additionalInfo;

    public UpdateRestaurantRequest(
            String name,
            String roadAddress,
            String detailAddress,
            Point coordinates,
            String openAt,
            String closeAt,
            String imageUrl,
            String additionalInfo
    ) {
        this.name = name;
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
        this.coordinates = coordinates;
        this.openAt = openAt;
        this.closeAt = closeAt;
        this.imageUrl = imageUrl;
        this.additionalInfo = additionalInfo;
    }
}
