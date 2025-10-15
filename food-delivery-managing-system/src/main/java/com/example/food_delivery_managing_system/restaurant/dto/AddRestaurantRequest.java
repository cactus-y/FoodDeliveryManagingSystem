package com.example.food_delivery_managing_system.restaurant.dto;

import com.example.food_delivery_managing_system.restaurant.Restaurant;
import com.example.food_delivery_managing_system.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddRestaurantRequest {
    private String name;
    private String roadAddress;
    private String detailAddress;
    private double longitude;
    private double latitude;
    private String openAt;
    private String closeAt;
    private String imageUrl;
    private String additionalInfo;
    private String username;

    public Restaurant toEntity(User user){
        return Restaurant.builder()
                .name(name)
                .roadAddress(roadAddress)
                .detailAddress(detailAddress)
                .coordinates(new GeometryFactory(new PrecisionModel(), 4326).createPoint(new Coordinate(longitude, latitude)))
                .openAt(openAt)
                .closeAt(closeAt)
                .imageUrl(imageUrl)
                .additionalInfo(additionalInfo)
                .user(user)
                .build();
    }
}
