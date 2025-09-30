package com.example.food_delivery_managing_system.user.dto;

import com.example.food_delivery_managing_system.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
@AllArgsConstructor
public class UserRequest {

    private final String email;
    private final String password;
    private final String name;
    private final String nickName;
    private final String roadAddress;
    private final String detailAddress;
    private final String coordinates;
    private final String profileUrl;

    public User toEntity(String encodedPassword) {
        return User.builder()
            .email(email)
            .password(encodedPassword)
            .name(name)
            .nickName(nickName)
            .roadAddress(roadAddress)
            .detailAddress(detailAddress)
            .coordinates(coordinates)
            .profileUrl(profileUrl)
            .build();
    }

}
