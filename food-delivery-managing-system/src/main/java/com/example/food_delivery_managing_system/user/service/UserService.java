package com.example.food_delivery_managing_system.user.service;

import com.example.food_delivery_managing_system.menu.MenuRepository;
import com.example.food_delivery_managing_system.menu.dto.MenuSummaryResponse;
import com.example.food_delivery_managing_system.user.dto.UserRequest;
import com.example.food_delivery_managing_system.user.dto.UserResponse;
import com.example.food_delivery_managing_system.user.entity.User;
import com.example.food_delivery_managing_system.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        User user = userRepository.findByEmail(email);
        return UserResponse.from(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByNickName(String nickName) {
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

}
