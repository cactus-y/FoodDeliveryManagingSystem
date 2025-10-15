// src/main/java/com/example/food_delivery_managing_system/user/controller/UserProfileController.java
package com.example.food_delivery_managing_system.user.controller;

import com.example.food_delivery_managing_system.S3.S3Service; // ← 네가 준 경로로 import
import com.example.food_delivery_managing_system.user.dto.PasswordChangeRequest;
import com.example.food_delivery_managing_system.user.dto.UserPatchRequest;
import com.example.food_delivery_managing_system.user.dto.UserResponse;
import com.example.food_delivery_managing_system.user.entity.CustomUserDetails;
import com.example.food_delivery_managing_system.user.service.UserAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserAuthService userAuthService;
    private final S3Service s3Service; // ← 네 S3Service 사용

    /** 내 정보 조회: DB에 저장된 profileUrl(공개 URL) 그대로 반환 */
    @GetMapping("/me")
    public UserResponse getMe(@AuthenticationPrincipal CustomUserDetails principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        Long userId = principal.getId();
        return userAuthService.getProfile(userId);
    }

    /** 내 정보 수정 (멀티파트): 이미지가 있으면 업로드 후 반환된 URL을 profileUrl로 저장 */
    @PatchMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateMe(@AuthenticationPrincipal CustomUserDetails principal,
                         @ModelAttribute UserPatchRequest request,
                         @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws Exception {

        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        Long userId = principal.getId();

        if (profileImage != null && !profileImage.isEmpty()) {
            // 업로드 후 공개 URL(String) 반환
            String uploadedUrl = s3Service.uploadFile(profileImage);
            request.setProfileUrl(uploadedUrl); // DB에는 공개 URL을 그대로 저장
        }

        userAuthService.updateUser(userId, request);
    }

    /** 비밀번호 변경 */
    @PatchMapping("/me/password")
    public void changePassword(@AuthenticationPrincipal CustomUserDetails principal,
                               @ModelAttribute @Valid PasswordChangeRequest request) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        Long userId = principal.getId();
        userAuthService.changePassword(userId, request);
    }
}
