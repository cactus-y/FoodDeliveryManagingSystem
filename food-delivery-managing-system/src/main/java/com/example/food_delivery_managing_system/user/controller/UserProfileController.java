// src/main/java/com/example/food_delivery_managing_system/user/controller/UserProfileController.java
package com.example.food_delivery_managing_system.user.controller;

import com.example.food_delivery_managing_system.file.domain.StoredFile;
import com.example.food_delivery_managing_system.file.service.S3StorageService;
import com.example.food_delivery_managing_system.user.dto.PasswordChangeRequest;
import com.example.food_delivery_managing_system.user.dto.UserPatchRequest;
import com.example.food_delivery_managing_system.user.dto.UserResponse;
import com.example.food_delivery_managing_system.user.eneity.CustomUserDetails;
import com.example.food_delivery_managing_system.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;
    private final S3StorageService s3;

    /** 내 정보 조회: profileImageUrl(절대 URL) 세팅해서 반환 */
    // UserProfileController.java (user 패키지의 컨트롤러)
    @GetMapping("/me")
    public UserResponse getMe(@AuthenticationPrincipal CustomUserDetails principal) {
        Long userId = principal.getId();
        UserResponse resp = userService.getProfile(userId);

        String key = resp.getProfileUrl(); // DB엔 S3 key 저장
        if (key != null && (resp.getProfileImageUrl() == null || resp.getProfileImageUrl().isBlank())) {
            resp.setProfileImageUrl(s3.signedGetUrl(key, 60)); // 60분짜리 서명 URL
        }
        return resp;
    }

    /** 내 정보 수정 (멀티파트) – 파일 있으면 S3 업로드 후 키를 profileUrl에 주입 */
    @PatchMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateMe(@AuthenticationPrincipal CustomUserDetails principal,
                         @ModelAttribute UserPatchRequest request,
                         @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws Exception {

        Long userId = principal.getId();

        if (profileImage != null && !profileImage.isEmpty()) {
            StoredFile sf = s3.uploadAndSave(profileImage, userId, "profiles/" + userId);
            request.setProfileUrl(sf.getS3Key()); // DB에는 “키” 저장
        }

        userService.updateUser(userId, request);
    }

    /** 비밀번호 변경 */
    @PatchMapping("/me/password")
    public void changePassword(@AuthenticationPrincipal CustomUserDetails principal,
                               @ModelAttribute @Valid PasswordChangeRequest request) {
        Long userId = principal.getId();
        userService.changePassword(userId, request);
    }
}
