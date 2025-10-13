package com.example.food_delivery_managing_system.admin;

import com.example.food_delivery_managing_system.admin.dto.UserListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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
    // @GetMapping("/api/admin/posts")

    // user 상태 수정
    // @PutMapping("/api/admin/users")

    // post 상태 수정
    // @PutMapping("/api/admin/posts")


}
