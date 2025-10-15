package com.example.food_delivery_managing_system.user.dto;

import com.example.food_delivery_managing_system.user.entity.User;
import com.example.food_delivery_managing_system.user.entity.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
// UserResponse.java
@Getter
@Builder(toBuilder = true) // 복사 빌더 활성화!
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
    private final String profileUrl;       // S3 key 저장
    private final String profileImageUrl;  // (선택) 서명 URL 응답용


    public static UserResponse from(User user) {
        // 좌표 null 안전 처리
        double lat = 0d;
        double lon = 0d;
        if (user.getCoordinates() != null) {
            lat = user.getCoordinates().getY();
            lon = user.getCoordinates().getX();
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
                .longitude(lon)
                .profileUrl(user.getProfileUrl())
                // profileImageUrl 는 컨트롤러에서 toBuilder()로 채움
                .build();
    }
}
