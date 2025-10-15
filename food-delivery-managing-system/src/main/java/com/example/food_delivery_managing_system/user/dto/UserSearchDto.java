package com.example.food_delivery_managing_system.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserSearchDto {
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private String email;

    @Builder
    public UserSearchDto(Long userId, String nickname, String profileImageUrl, String email) {
        this.userId = userId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.email = email;
    }
}
