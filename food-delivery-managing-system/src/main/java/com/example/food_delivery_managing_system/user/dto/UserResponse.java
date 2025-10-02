package com.example.food_delivery_managing_system.user.dto;

import com.example.food_delivery_managing_system.user.User;
import com.example.food_delivery_managing_system.user.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserResponse {

    private final Long userId;
    private final String email;
    private final String name;
    private final UserRole userRole;
    private final String nickName;
    private final String roadAddress;
    private final String detailAddress;
    private final double latitude;
    private final double longitude;
    private final String profileUrl;

    @Builder
    public UserResponse(Long userId
        , String email
        , String name
        , UserRole userRole
        , String nickName
        , String roadAddress
        , String detailAddress
        , double latitude
        , double longitude
        , String profileUrl) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.userRole = userRole;
        this.nickName = nickName;
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.profileUrl = profileUrl;
    }

    public static UserResponse from(User user) {
        return UserResponse.builder()
            .userId(user.getUserId())
            .email(user.getEmail())
            .name(user.getName())
            .userRole(user.getUserRole())
            .nickName(user.getNickName())
            .roadAddress(user.getRoadAddress())
            .detailAddress(user.getDetailAddress())
            .latitude(user.getCoordinates().getY())
            .longitude(user.getCoordinates().getX())
            .profileUrl(user.getProfileUrl())
            .build();
    }
}
