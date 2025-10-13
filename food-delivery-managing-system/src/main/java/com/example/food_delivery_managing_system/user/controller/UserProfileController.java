package com.example.food_delivery_managing_system.user.controller;

import com.example.food_delivery_managing_system.user.dto.PasswordChangeRequest;
import com.example.food_delivery_managing_system.user.dto.UserPatchRequest;
import com.example.food_delivery_managing_system.user.dto.UserResponse;
import com.example.food_delivery_managing_system.user.eneity.CustomUserDetails;
import com.example.food_delivery_managing_system.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api") // <-- /api/me, /api/me/password 만들기 위함
@RequiredArgsConstructor
@Validated
public class UserProfileController {

    private final UserService userService;

    // GET /api/me : 내 정보 조회
    @GetMapping("/me")
    @ResponseBody // JSON으로 주고 싶으면 유지, 뷰로 보낼 거면 제거하고 Model 사용
    public UserResponse getMe(@AuthenticationPrincipal CustomUserDetails principal) {
        Long userId = principal.getId();
        return userService.getProfile(userId);
    }

    // PATCH /api/me : 내 정보 수정(프로필 이미지 포함 가능)
    @PatchMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public void updateMe(@AuthenticationPrincipal CustomUserDetails principal,
                         @ModelAttribute UserPatchRequest request,
                         @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {

        Long userId = principal.getId();

        // 프로필 이미지 파일이 있으면 업로드 처리 후 URL을 request.setProfileUrl()에 넣기
        if (profileImage != null && !profileImage.isEmpty()) {
            // TODO: S3 업로드 후 실제 URL로 대체
            String profileImageUrl = profileImage.getOriginalFilename();
            request.setProfileUrl(profileImageUrl);
        }

        userService.updateUser(userId, request);
    }

    // PATCH /api/me/password : 비밀번호 변경
    @PatchMapping("/me/password")
    @ResponseBody
    public void changePassword(@AuthenticationPrincipal CustomUserDetails principal,
                               @ModelAttribute @Validated PasswordChangeRequest request) {
        Long userId = principal.getId();
        userService.changePassword(userId, request);
    }
}
