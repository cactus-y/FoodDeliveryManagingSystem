package com.example.food_delivery_managing_system.user.dto;

import com.example.food_delivery_managing_system.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

@Getter
@Setter
@AllArgsConstructor
public class UserRequest {

    private final String email;
    private final String password;
    private final String name;
    private final String nickName;
    private final String roadAddress;
    private final String detailAddress;
    private final double latitude;
    private final double longitude;
    private String profileUrl;

    public User toEntity(String encodedPassword) {
        return User.builder()
            .email(email)
            .password(encodedPassword)
            .name(name)
            .nickName(nickName)
            .roadAddress(roadAddress)
            .detailAddress(detailAddress)
            .coordinates(new GeometryFactory().createPoint(new Coordinate(longitude, latitude)))
            .profileUrl(profileUrl)
            .build();
    }

}
