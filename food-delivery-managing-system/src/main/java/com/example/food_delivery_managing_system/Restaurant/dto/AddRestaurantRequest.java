package com.example.food_delivery_managing_system.Restaurant.dto;

import com.example.food_delivery_managing_system.Restaurant.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddRestaurantRequest {
    private String name;
    private String roadAddress;
    private String detailAddress;
    private Point coordinates;
    private String openAt;
    private String closeAt;
    private String imageUrl;
    private String additionalInfo;

    public Restaurant toEntity(){
        return Restaurant.builder()
                .name(name)
                .roadAddress(roadAddress)
                .detailAddress(detailAddress)
                .coordinates(coordinates)
                .openAt(openAt)
                .closeAt(closeAt)
                .imageUrl(imageUrl)
                .additionalInfo(additionalInfo)
                .build();
    }
}
