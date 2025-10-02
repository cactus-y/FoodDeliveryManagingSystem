package com.example.food_delivery_managing_system.chat.dto;

import com.example.food_delivery_managing_system.chat.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OpponentUser {
    private Long userId;
    private String nickname;
    private String profileImageUrl;

    // JPQL Constructor Expression용
    public OpponentUser(Long userId, String nickname, String profileImageUrl) {
        this.userId = userId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }
    @Builder
    public OpponentUser(User user) {
        this.userId = user.getId();
        this.nickname = user.getNickname();
        this.profileImageUrl = user.getProfileImageUrl();
    }

}
