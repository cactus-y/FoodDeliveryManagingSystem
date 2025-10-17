package com.example.food_delivery_managing_system.user.controller;

import com.example.food_delivery_managing_system.S3.S3Service;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserAuthService userAuthService;
    private final S3Service s3Service; // 주입만, 직접 사용은 서비스에서

    /** 내 정보 조회 */
    @GetMapping("/me")
    public UserResponse getMe(@AuthenticationPrincipal CustomUserDetails principal) {
        if (principal == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return userAuthService.getProfile(principal.getId());
    }

    /**
     * 내 정보 수정 (multipart/form-data)
     * - 파일/텍스트를 한 DTO로 받음(@ModelAttribute)
     * - 파일이 존재하면 업로드/교체, removeProfileImage=true면 기본이미지/삭제 정책 적용
     */
    @PatchMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserResponse updateMe(@AuthenticationPrincipal CustomUserDetails principal,
                                 @ModelAttribute UserPatchRequest request) throws IOException {
        if (principal == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return UserResponse.from(userAuthService.updateUser(principal.getId(), request));
    }

    /** 비밀번호 변경 */
    @PatchMapping("/me/password")
    public void changePassword(@AuthenticationPrincipal CustomUserDetails principal,
                               @ModelAttribute @Valid PasswordChangeRequest request) {
        if (principal == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        userAuthService.changePassword(principal.getId(), request);
    }
}
