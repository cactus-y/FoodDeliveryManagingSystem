package com.example.food_delivery_managing_system.restaurant.dto;

import lombok.Getter;
import org.locationtech.jts.geom.Point;

@Getter
public class UpdateRestaurantRequest {
    private String name;
    private String roadAddress;
    private String detailAddress;
    private double longitude;
    private double latitude;
    private String openAt;
    private String closeAt;
    private String imageUrl;
    private String additionalInfo;

    public UpdateRestaurantRequest(
            String name,
            String roadAddress,
            String detailAddress,
            double longitude,
            double latitude,
            String openAt,
            String closeAt,
            String imageUrl,
            String additionalInfo
    ) {
        this.name = name;
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
        this.longitude = longitude;
        this.latitude = latitude;
        this.openAt = openAt;
        this.closeAt = closeAt;
        this.imageUrl = imageUrl;
        this.additionalInfo = additionalInfo;
    }
}