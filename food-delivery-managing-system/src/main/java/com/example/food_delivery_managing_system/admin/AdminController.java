package com.example.food_delivery_managing_system.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    // 메인 관리자 페이지 (사이드바 포함)
    @GetMapping("/main")
    public String adminMain() {
        return "admin/adminMain";
    }
    @GetMapping("/login")
    public String adminLogin() {
        return "admin/adminLogin";
    }
}
