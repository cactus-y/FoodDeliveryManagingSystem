package com.example.food_delivery_managing_system.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserViewController {

    @GetMapping("/login")
    public String login() {
        return "/user/login";
    }

    @GetMapping("/api/users/signup")
    public String signup() {
        return "/user/user_signup";
    }

    @GetMapping("/map")
    public String mapView() {
        return "/user/map";
    }

}
