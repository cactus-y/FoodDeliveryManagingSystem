package com.example.food_delivery_managing_system.user.dto;

import com.example.food_delivery_managing_system.user.User;
import com.example.food_delivery_managing_system.user.UserRole;
import java.time.LocalDateTime;
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
    private final String coordinates;
//    private final LocalDateTime createdAt;
//    private final LocalDateTime updatedAt;
    private final String profileUrl;

    @Builder
    public UserResponse(Long userId
        , String email
        , String name
        , UserRole userRole
        , String nickName
        , String roadAddress
        , String detailAddress
        , String coordinates
//        , LocalDateTime createdAt
//        , LocalDateTime updatedAt
        , String profileUrl) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.userRole = userRole;
        this.nickName = nickName;
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
        this.coordinates = coordinates;
//        this.createdAt = createdAt;
//        this.updatedAt = updatedAt;
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
            .coordinates(user.getCoordinates())
//            .createdAt(user.getCreatedAt())
//            .updatedAt(user.getUpdatedAt())
            .profileUrl(user.getProfileUrl())
            .build();
    }
}
