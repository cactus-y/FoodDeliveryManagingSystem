package com.example.food_delivery_managing_system.admin;

import com.example.food_delivery_managing_system.admin.dto.AdminRestaurantListResponse;
import com.example.food_delivery_managing_system.admin.dto.RestaurantStatusResponse;
import com.example.food_delivery_managing_system.admin.dto.UserListResponse;
import com.example.food_delivery_managing_system.admin.dto.UserStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminRestController {
    private final AdminService adminService;

    // service 기능에 맞는 api 설계

    // 모든 user 조회
    @GetMapping("/api/admin/users")
    public List<UserListResponse> getUsers() {
        List<UserListResponse> users = adminService.getUsers();
        return users;
    }

    // 모든 post 조회
    @GetMapping("/api/admin/posts")
    public List<AdminRestaurantListResponse> getPostList() {
        return adminService.getAllRestaurants();
    }

    // 사용자 상태 update
    @PutMapping("/api/users/{email}/status")  // 식별을 위한 eamil 은 js 에서 추출해 url 경로에 포함하는 방식으로
    public ResponseEntity<UserStatusResponse> toggleUserStatus(@PathVariable String email) {
        UserStatusResponse response = adminService.updateUserStatus(email);
        return ResponseEntity.ok(response);
    }

    // 레스토랑 상태 update
    @PutMapping("/api/restaurants/{email}/{name}/status")
    public ResponseEntity<RestaurantStatusResponse> toggleRestaurantStatus(@PathVariable String email, @PathVariable String name) {
        RestaurantStatusResponse response = adminService.updateRestaurantStatus(email, name);
        return ResponseEntity.ok(response);
    }


}
