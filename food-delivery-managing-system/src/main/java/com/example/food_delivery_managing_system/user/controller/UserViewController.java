package com.example.food_delivery_managing_system.user.controller;

import com.example.food_delivery_managing_system.user.dto.UserRequest;
import com.example.food_delivery_managing_system.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class UserViewController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "/user/login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "/user/user_signup";
    }

    @PostMapping("/signup")
    public String addOwner(@ModelAttribute UserRequest userRequest, @RequestParam("profileImage") MultipartFile profileImage) {

        // 아직 AWS s3 저장 경로를 모르기 때문에 대충 저장
        String profileImageUrl = profileImage.getOriginalFilename(); // 나중에 s3 저장 경로로 변경
        userRequest.setProfileUrl(profileImageUrl); // String 으로 변경

        userService.addOwner(userRequest);
        return "redirect:/user/login";
    }

    @GetMapping("/map")
    public String mapView() {
        return "/user/map";
    }

}
