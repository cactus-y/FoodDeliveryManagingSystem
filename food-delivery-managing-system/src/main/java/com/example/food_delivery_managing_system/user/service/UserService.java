package com.example.food_delivery_managing_system.user.service;

import com.example.food_delivery_managing_system.menu.MenuRepository;
import com.example.food_delivery_managing_system.menu.dto.MenuSummaryResponse;
import com.example.food_delivery_managing_system.user.dto.UserRequest;
import com.example.food_delivery_managing_system.user.dto.UserResponse;
import com.example.food_delivery_managing_system.user.dto.UserSearchDto;
import com.example.food_delivery_managing_system.user.entity.User;
import com.example.food_delivery_managing_system.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

    public void addOwner(UserRequest userRequest) {
        String encodedPassword = bCryptPasswordEncoder.encode(userRequest.getPassword());
        User user = userRepository.save(userRequest.toEntity(encodedPassword));
        UserResponse.from(user);
    }

    public UserResponse getUserDetails(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));        return UserResponse.from(user);
    }

    public boolean existsByEmail(String email) {
        boolean checkEmail = userRepository.existsByEmail(email);
        return userRepository.existsByEmail(email);
    }

    public boolean existsByNickName(String nickName) {
        boolean checkNickName = userRepository.existsByNickName(nickName);
        return userRepository.existsByNickName(nickName);
    }

    public List<MenuSummaryResponse> getListOfMenus(Long restaurantId){
        return menuRepository.findAllByRestaurant_RestaurantIdx(restaurantId).stream()
            .map(m -> MenuSummaryResponse.builder()
                .menuIdx(m.getMenuIdx())
                .name(m.getName())
                .price(m.getPrice())
                .isSignature(m.getIsSignature())
                .imageUrl(m.getImageUrl())
                .createdAt(m.getCreatedAt())
                .build()
            ).toList();
    }

    public UserSearchDto findUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return UserSearchDto.builder()
                .userId(user.getUserId())
                .nickname(user.getNickName())
                .profileImageUrl(user.getProfileUrl())
                .email(user.getEmail())
                .build();
    }

}
