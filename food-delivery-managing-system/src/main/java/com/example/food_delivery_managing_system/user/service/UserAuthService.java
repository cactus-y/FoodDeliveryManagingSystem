package com.example.food_delivery_managing_system.user.service;

import com.example.food_delivery_managing_system.user.dto.LoginRequestDto;
import com.example.food_delivery_managing_system.user.dto.LoginResponseDto;
import com.example.food_delivery_managing_system.user.dto.PasswordChangeRequest;
import com.example.food_delivery_managing_system.user.dto.UserPatchRequest;
import com.example.food_delivery_managing_system.user.dto.UserResponse;
import com.example.food_delivery_managing_system.user.entity.CustomUserDetails;
import com.example.food_delivery_managing_system.user.entity.User;
import com.example.food_delivery_managing_system.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /* ===================== 로그인 / 로그아웃 ===================== */

    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto req, HttpServletRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        // SecurityContext + Session 저장
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        request.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context
        );

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        User user = userRepository.findById(principal.getId()).orElseThrow(); // principal.getId() == users.user_idx
        return LoginResponseDto.from(user);
    }

    public void logout(HttpServletRequest httpRequest) {
        SecurityContextHolder.clearContext();
        HttpSession session = httpRequest.getSession(false);
        if (session != null) session.invalidate();
    }

    /* ===================== 프로필 조회 / 수정 / 비밀번호 변경 ===================== */

    @Transactional(readOnly = true)
    public UserResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
        return UserResponse.from(user); // ← 불변 DTO 팩토리 사용
    }

    @Transactional
    public void updateUser(Long userId, UserPatchRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        // 부분 수정: null/blank 아닌 값만 반영
        if (StringUtils.hasText(req.getNickName())) {
            user.setNickName(req.getNickName());
        }
        if (StringUtils.hasText(req.getRoadAddress())) {
            user.setRoadAddress(req.getRoadAddress());
        }
        if (StringUtils.hasText(req.getDetailAddress())) {
            user.setDetailAddress(req.getDetailAddress());
        }
        Point coordinates = req.getCoordinates();
        if (coordinates != null) {
            user.setCoordinates(coordinates); // PostGIS(Point, 4326)
        }
        if (StringUtils.hasText(req.getProfileUrl())) {
            user.setProfileUrl(req.getProfileUrl()); // S3 key
        }

        userRepository.save(user);
    }

    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "current password mismatch");
        }
        if (StringUtils.hasText(req.getNewPasswordConfirm())
                && !req.getNewPassword().equals(req.getNewPasswordConfirm())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password confirm mismatch");
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }
}
