package com.example.food_delivery_managing_system.user.service;

import com.example.food_delivery_managing_system.user.dto.PasswordChangeRequest;
import com.example.food_delivery_managing_system.user.dto.UserPatchRequest;
import com.example.food_delivery_managing_system.user.dto.UserRequest;
import com.example.food_delivery_managing_system.user.dto.UserResponse;
import com.example.food_delivery_managing_system.user.eneity.User;
import com.example.food_delivery_managing_system.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

    public void addOwner(UserRequest userRequest) {
        String encodedPassword = bCryptPasswordEncoder.encode(userRequest.getPassword());
        User user = userRepository.save(userRequest.toEntity(encodedPassword));
        UserResponse.from(user);
    }

    public boolean existsByEmail(String email) {
        boolean checkEmail = userRepository.existsByEmail(email);
        return userRepository.existsByEmail(email);
    }

    public boolean existsByNickName(String nickName) {
        boolean checkNickName = userRepository.existsByNickName(nickName);
        return userRepository.existsByNickName(nickName);
    }

    // ===== 내 정보 조회 (마이페이지/수정폼 바인딩용) =====
    public UserResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return UserResponse.from(user);
    }

    @Transactional
    public void updateUser(Long userId, UserPatchRequest req) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (req.getNickName() != null && !req.getNickName().isBlank()) {
            // (선택) 다른 사용자 닉네임 중복 체크가 필요하면 여기서 검사
            u.setNickName(req.getNickName());
        }
        if (req.getRoadAddress() != null && !req.getRoadAddress().isBlank()) {
            u.setRoadAddress(req.getRoadAddress());
        }
        if (req.getDetailAddress() != null && !req.getDetailAddress().isBlank()) {
            u.setDetailAddress(req.getDetailAddress());
        }
        if (req.getProfileUrl() != null && !req.getProfileUrl().isBlank()) {
            u.setProfileUrl(req.getProfileUrl());
        }
        // 좌표가 들어오는 구조라면
        if (req.getCoordinates() != null) {
            u.setCoordinates(req.getCoordinates()); // SRID(4326) 맞춰서 세팅된 객체를 넣어주세요.
        }
    }

    // ===== 비밀번호 변경 =====
    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest req) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 현재 비밀번호 검증
        if (!bCryptPasswordEncoder.matches(req.getCurrentPassword(), u.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        // 새 비밀번호 일치 확인
        if (!req.getNewPassword().equals(req.getNewPasswordConfirm())) {
            throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다.");
        }
        // 암호화 후 저장
        u.setPassword(bCryptPasswordEncoder.encode(req.getNewPassword()));
    }
}
