package com.example.food_delivery_managing_system.admin;


import com.example.food_delivery_managing_system.admin.dto.AdminRestaurantListResponse;
import com.example.food_delivery_managing_system.admin.dto.RestaurantStatusResponse;
import com.example.food_delivery_managing_system.admin.dto.UserListResponse;
import com.example.food_delivery_managing_system.admin.dto.UserStatusResponse;
import com.example.food_delivery_managing_system.admin.repository.AdminRestaurantRepository;
import com.example.food_delivery_managing_system.admin.repository.AdminRepository;
import com.example.food_delivery_managing_system.menu.MenuRepository;
import com.example.food_delivery_managing_system.restaurant.Restaurant;
import com.example.food_delivery_managing_system.menu.Menu;
import com.example.food_delivery_managing_system.restaurant.RestaurantRepository;
import com.example.food_delivery_managing_system.restaurant.dto.RestaurantStatus;
import com.example.food_delivery_managing_system.user.entity.User;
import com.example.food_delivery_managing_system.user.entity.UserStatus;
import com.example.food_delivery_managing_system.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final AdminRestaurantRepository adminRestaurantRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    // Users 조회
    public List<UserListResponse> getUsers() {
        return adminRepository.findAllUserListWithRestaurantName();
    }

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
                            longitude,
                            r.getUser().getEmail(),
                            r.getRoadAddress()
                    );
                })
                .toList();
    }
    // User Status 변경(회원, 탈퇴)
    @Transactional
    public UserStatusResponse updateUserStatus(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));

        UserStatus currentStatus = user.getUserStatus();
        UserStatus newStatus = currentStatus == UserStatus.ACTIVE
                ? UserStatus.INACTIVE
                : UserStatus.ACTIVE;

        user.update(newStatus);
        userRepository.save(user);

        return UserStatusResponse.builder()
                .email(email)
                .name(user.getName())
                .currentStatus(newStatus.toString())
                .message(user.getName() + " 사용자 상태가 " + newStatus + "로 변경되었습니다.")
                .build();
    }

    // Post Status 변경(공개, 비공개)
    public RestaurantStatusResponse updateRestaurantStatus(String email, String name) {
        Restaurant restaurant = restaurantRepository.findByUserEmailAndName(email, name)
                .orElseThrow(() -> new IllegalArgumentException(
                        "레스토랑을 찾을 수 없습니다: " + email + ", " + name));

        RestaurantStatus previousStatus = restaurant.getRestaurantStatus();
        RestaurantStatus newStatus = previousStatus == RestaurantStatus.ACTIVE
                ? RestaurantStatus.INACTIVE
                : RestaurantStatus.ACTIVE;

        // Entity의 toggleStatus 메서드 호출
        restaurant.update(newStatus);
        restaurantRepository.save(restaurant);

        return RestaurantStatusResponse.builder()
                .email(email)
                .restaurantName(restaurant.getName())
                .previousStatus(previousStatus.toString())
                .currentStatus(restaurant.getRestaurantStatus().toString())
                .message(restaurant.getName() + " 레스토랑 상태가 "
                        + restaurant.getRestaurantStatus() + "로 변경되었습니다.")
                .build();
    }

    // 통계
    public Map<String, Integer> getRestaurantsByRegion() {

        List<Restaurant> restaurants = restaurantRepository.findAll();
        Map<String, Integer> regionCount = new LinkedHashMap<>();

        for (Restaurant r : restaurants) {
            String roadAddress = r.getRoadAddress();
            if (roadAddress != null && !roadAddress.isEmpty()) {
                String region = extractRegion(roadAddress);

                if (!"-".equals(region)) {
                    regionCount.put(region, regionCount.getOrDefault(region, 0) + 1);
                }
            }
        }

        return regionCount;
    }

    private String extractRegion(String roadAddress) {
        if (roadAddress == null || roadAddress.isEmpty()) {
            return "-";
        }

        String cleanedAddress = roadAddress.replaceAll("(?i)대한민국\\s*", "");
        String[] parts = cleanedAddress.trim().split("\\s+");


        if (parts.length == 0 || parts[0].isEmpty()) {
            return "-";
        }

        String firstPart = parts[0];
        int limit = 2;

        if (firstPart.endsWith("도")) {
            limit = 3;
        }

        StringBuilder regionBuilder = new StringBuilder();
        for (int i = 0; i < Math.min(parts.length, limit); i++) {
            if (i > 0) {
                regionBuilder.append(" ");
            }
            regionBuilder.append(parts[i]);
        }

        String result = regionBuilder.toString().trim();
        return result.isEmpty() ? "-" : result;
    }
}