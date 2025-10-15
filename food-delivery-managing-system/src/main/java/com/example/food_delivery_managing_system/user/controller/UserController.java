package com.example.food_delivery_managing_system.user.controller;

import com.example.food_delivery_managing_system.chat.dto.ChatUserDto;
import com.example.food_delivery_managing_system.user.service.UserService;
import com.example.food_delivery_managing_system.user.dto.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/check-email")
    @ResponseBody
    public boolean checkEmail(@RequestParam String email) {
        return userService.existsByEmail(email);
    }

    @GetMapping("/nick-name")
    @ResponseBody
    public boolean checkNickName(@RequestParam String nickName) {
        return userService.existsByNickName(nickName);
    }

    @PostMapping("/signup")
    public String addOwner(@ModelAttribute UserRequest userRequest, @RequestParam("profileImage") MultipartFile profileImage) {

        // 아직 AWS s3 저장 경로를 모르기 때문에 대충 저장
        String profileImageUrl = profileImage.getOriginalFilename(); // 나중에 s3 저장 경로로 변경
        userRequest.setProfileUrl(profileImageUrl); // String 으로 변경

        userService.addOwner(userRequest);
        return "redirect:/user/login";
    }

    // 유저 검색을 쓸 일이 생겨서 추가합니다..
    @GetMapping
    public ResponseEntity<ChatUserDto> findUserByEmail(@RequestParam("email") String email) {
        ChatUserDto response = userService.findUserByEmail(email);
        return ResponseEntity.ok(response);
    }
}
