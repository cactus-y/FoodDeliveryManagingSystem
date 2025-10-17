package com.example.food_delivery_managing_system.user.dto;

import com.example.food_delivery_managing_system.user.entity.User;
import com.example.food_delivery_managing_system.user.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private Long userId;
    private String name;
    private UserRole role;
    private String message;


    public static LoginResponseDto from(User user) {
        return new LoginResponseDto(
                user.getUserId(),
                user.getName(),
                user.getUserRole(),
                "login success"
        );
    }

}
