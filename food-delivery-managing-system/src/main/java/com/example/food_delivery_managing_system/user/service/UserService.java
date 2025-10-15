package com.example.food_delivery_managing_system.user.service;

import com.example.food_delivery_managing_system.chat.dto.ChatUserDto;
import com.example.food_delivery_managing_system.user.dto.UserRequest;
import com.example.food_delivery_managing_system.user.dto.UserResponse;
import com.example.food_delivery_managing_system.user.entity.User;
import com.example.food_delivery_managing_system.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

    public void addOwner(UserRequest userRequest) {
        String encodedPassword = bCryptPasswordEncoder.encode(userRequest.getPassword());
        User user = userRepository.save(userRequest.toEntity(encodedPassword));
        UserResponse.from(user);
    }

    public boolean existsByEmail(String email) {
        boolean checkEmail = userRepository.existsByEmail(email);
        return userRepository.existsByEmail(email);
    }

    public boolean existsByNickName(String nickName) {
        boolean checkNickName = userRepository.existsByNickName(nickName);
        return userRepository.existsByNickName(nickName);
    }

    public ChatUserDto findUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return ChatUserDto.builder()
                .userId(user.getUserId())
                .nickname(user.getNickName())
                .profileImageUrl(user.getProfileUrl())
                .build();
    }

}
