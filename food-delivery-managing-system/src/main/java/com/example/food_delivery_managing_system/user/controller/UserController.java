package com.example.food_delivery_managing_system.user.controller;

import com.example.food_delivery_managing_system.user.UserService;
import com.example.food_delivery_managing_system.user.dto.UserRequest;
import com.example.food_delivery_managing_system.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/users/check-email")
    @ResponseBody
    public boolean checkEmail(@RequestParam String email) {
        return userService.existsByEmail(email);
    }

    @GetMapping("/api/users/nick-name")
    @ResponseBody
    public boolean checkNickName(@RequestParam String nickName) {
        return userService.existsByNickName(nickName);
    }

//    @PostMapping("/api/users/signup")
//    public ResponseEntity<UserResponse> addOwner(@RequestBody UserRequest userRequest) {
//        UserResponse userResponse = userService.addOwner(userRequest);
//        return ResponseEntity.status(HttpStatus.CREATED)
//            .body(userResponse);
//    }

    @PostMapping("/api/users/signup")
    public String addOwner(@ModelAttribute UserRequest userRequest) {
        userService.addOwner(userRequest);
        return "redirect:/user/login";
    }
}
