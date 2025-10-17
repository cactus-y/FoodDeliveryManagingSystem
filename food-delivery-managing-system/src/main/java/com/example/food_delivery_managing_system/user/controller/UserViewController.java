package com.example.food_delivery_managing_system.user.controller;

import com.example.food_delivery_managing_system.S3.S3Service;
import com.example.food_delivery_managing_system.user.dto.UserRequest;
import com.example.food_delivery_managing_system.user.service.UserService;
import java.io.IOException;
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
    private final S3Service s3Service;

    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "user/user_signup";
    }

    @PostMapping("/signup")
    public String addOwner(@ModelAttribute UserRequest userRequest, @RequestParam("profileImage") MultipartFile profileImage)
        throws IOException {

        if (profileImage.isEmpty()) {
            userRequest.setProfileUrl("/images/default-user-profile.png");
            userService.addOwner(userRequest);
        } else {
            String fileUrl = s3Service.uploadFile(profileImage);
            System.out.println("저장된 이미지 URL : " + fileUrl);
            userRequest.setProfileUrl(fileUrl);
            userService.addOwner(userRequest);
        }
        return "redirect:/login";

    }

    @GetMapping("/map")
    public String mapView() {
        return "user/map";
    }

    @GetMapping("/users/profile/edit")
    public String editProfile() {
        return "user/profile_edit";
    }

    @GetMapping("/users/me")
    public String myInfo() {
        return "user/my_info";
    }
}
