package com.example.food_delivery_managing_system.user.dto;

import com.example.food_delivery_managing_system.user.eneity.User;
import com.example.food_delivery_managing_system.user.eneity.UserRole;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserResponse {

    private Long userId;
    private String email;
    private String name;
    private UserRole userRole;
    private String nickName;
    private String roadAddress;
    private String detailAddress;
    private Double latitude;
    private Double longitude;
    private String profileUrl;
    private String profileImageUrl;

    public static UserResponse from(User user) {
        Double lat = null;
        Double lng = null;
        if (user.getCoordinates() != null) {
            lat = user.getCoordinates().getY();
            lng = user.getCoordinates().getX();
        }

        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .userRole(user.getUserRole())
                .nickName(user.getNickName())
                .roadAddress(user.getRoadAddress())
                .detailAddress(user.getDetailAddress())
                .latitude(lat)
                .longitude(lng)
                .profileUrl(user.getProfileUrl())
                .build();
    }
}
