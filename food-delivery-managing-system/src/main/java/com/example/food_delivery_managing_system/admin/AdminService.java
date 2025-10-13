package com.example.food_delivery_managing_system.admin;


import com.example.food_delivery_managing_system.admin.dto.PostListResponse;
import com.example.food_delivery_managing_system.admin.dto.UserListResponse;
import com.example.food_delivery_managing_system.admin.repository.AdminRestaurauntRepository;
import com.example.food_delivery_managing_system.admin.repository.AdminRepository;
import com.example.food_delivery_managing_system.menu.MenuRepository;
import com.example.food_delivery_managing_system.menu.Menu;
import com.example.food_delivery_managing_system.restaurant.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final AdminRestaurauntRepository adminRestaurauntRepository;
    private final MenuRepository menuRepository;

    // Users 조회
    public List<UserListResponse> getUsers() {
        return adminRepository.findAllUserListWithRestaurantName();
    }

    // User Status 변경(회원, 탈퇴)

    // Posts 조회. id 조회 안해도 될듯? 어짜피 생성순 내림차순으로 정렬할거라
    public List<PostListResponse> getAllPostList() {
        List<Restaurant> restaurants = adminRestaurauntRepository.findAllRestaurantsOrderByCreatedAt();

        return restaurants.stream()
                .map(r -> {
                    // 대표 메뉴 조회
                    String signatureMenu = menuRepository.findFirstByRestaurantAndIsSignatureOrderByName(r, "Y")
                            .map(Menu::getName)
                            .orElse(null);

                    return new PostListResponse(
                            r.getName(),
                            signatureMenu,
                            r.getCreatedAt(),
                            r.getRestaurantStatus(),
                            r.getCoordinates()
                    );
                })
                .toList();
    }
    // User Status 변경(회원, 탈퇴)

    // Post Status 변경(공개, 비공개)

}
