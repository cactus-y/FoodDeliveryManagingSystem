package com.example.food_delivery_managing_system.admin;


import com.example.food_delivery_managing_system.admin.dto.AdminRestaurantListResponse;
import com.example.food_delivery_managing_system.admin.dto.UserListResponse;
import com.example.food_delivery_managing_system.admin.repository.AdminRestaurantRepository;
import com.example.food_delivery_managing_system.admin.repository.AdminRepository;
import com.example.food_delivery_managing_system.menu.MenuRepository;
import com.example.food_delivery_managing_system.restaurant.Restaurant;
import com.example.food_delivery_managing_system.menu.Menu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final AdminRestaurantRepository adminRestaurantRepository;
    private final MenuRepository menuRepository;

    // Users 조회
    public List<UserListResponse> getUsers() {
        return adminRepository.findAllUserListWithRestaurantName();
    }

    // User Status 변경(회원, 탈퇴)

    // Posts 조회
    public List<AdminRestaurantListResponse> getAllRestaurants() {
        List<Restaurant> restaurants = adminRestaurantRepository.findAllRestaurantsOrderByCreatedAt();

        return restaurants.stream()
                .map(r -> {
                    // 대표 메뉴 조회
                    String signatureMenu = menuRepository
                            .findFirstByRestaurantAndIsSignatureOrderByName(r, "Y")
                            .map(Menu::getName)
                            .orElse(null);

                    // Point에서 x, y 추출
                    Double latitude = r.getCoordinates().getY();
                    Double longitude = r.getCoordinates().getX();

                    return new AdminRestaurantListResponse(
                            r.getRestaurantIdx(),
                            r.getName(),
                            signatureMenu,
                            r.getCreatedAt(),
                            r.getRestaurantStatus(),
                            latitude,
                            longitude
                    );
                })
                .toList();
    }
    // User Status 변경(회원, 탈퇴)
/*    public UserListResponse updateUserStatus(UserListResponse users) {

    }*/


    // Post Status 변경(공개, 비공개)

}
