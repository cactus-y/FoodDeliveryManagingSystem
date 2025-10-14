package com.example.food_delivery_managing_system.restaurant.dto;

import com.example.food_delivery_managing_system.restaurant.Restaurant;
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
    // private User user;

    public Restaurant toEntity(/* User user */){
        return Restaurant.builder()
                .name(name)
                .roadAddress(roadAddress)
                .detailAddress(detailAddress)
                .coordinates(new Point(coordinates))
                .openAt(openAt)
                .closeAt(closeAt)
                .imageUrl(imageUrl)
                .additionalInfo(additionalInfo)
                // .user(user)
                .build();
    }
}
