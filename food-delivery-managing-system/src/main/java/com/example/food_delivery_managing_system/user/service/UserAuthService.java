package com.example.food_delivery_managing_system.user.service;

import com.example.food_delivery_managing_system.S3.S3Service;
import com.example.food_delivery_managing_system.user.dto.*;
import com.example.food_delivery_managing_system.user.entity.CustomUserDetails;
import com.example.food_delivery_managing_system.user.entity.User;
import com.example.food_delivery_managing_system.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service; // ★ S3 업로드/삭제

    /**
     * 권장: @Bean 주입. 여기서는 fallback.
     */
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    /**
     * 기본 프로필 이미지 공개 URL
     * - 정책 A: null 저장 후 뷰에서 기본이미지로 fallback → 기본값을 null로 두고 isDefaultUrl()은 false로 처리
     * - 정책 B(여기 채택 예시): 기본이미지의 고정 공개 URL을 설정
     */
    @Value("${app.profile.default-url:/images/default-user-profile.png}")
    private String defaultProfileUrl;

    /* ===================== 로그인 / 로그아웃 ===================== */

    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto req, HttpServletRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        request.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context
        );

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        User user = userRepository.findById(principal.getId()).orElseThrow();
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
        return UserResponse.from(user);
    }

    /**
     * 부분 수정
     * - 문자열: hasText일 때만 반영
     * - 좌표: (lat,lng) 둘 다 들어오면 Point(4326)
     * - 이미지:
     *    1) removeProfileImage=true → 기본이미지로 전환(or null) + 이전 S3 삭제
     *    2) profileImage 업로드 → S3 업로드 + URL 교체 + 이전 S3 삭제
     *    3) 아무 것도 없으면 기존 유지
     */
    @Transactional
    public User updateUser(Long userId, UserPatchRequest req) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        // ===== 텍스트/좌표 =====
        setIfHasText(req.getNickName(), user::setNickName);
        setIfHasText(req.getRoadAddress(), user::setRoadAddress);
        setIfHasText(req.getDetailAddress(), user::setDetailAddress);

        Double lat = req.getLatitude();
        Double lng = req.getLongitude();
        if (lat != null && lng != null) {
            Point p = geometryFactory.createPoint(new Coordinate(lng, lat));
            p.setSRID(4326);
            user.setCoordinates(p);
        } else if (req.getCoordinates() != null) {
            Point p = req.getCoordinates();
            if (p.getSRID() == 0) p.setSRID(4326);
            user.setCoordinates(p);
        }

        // ===== 이미지 =====
        String oldUrl = user.getProfileUrl();                   // 이전 이미지 URL
        MultipartFile newFile = req.getProfileImage();          // 새 업로드 파일
        Boolean wantRemove = req.getRemoveProfileImage();       // 삭제 플래그(선택)

        // 1) 삭제 플래그 우선 처리 (원치 않으면 제거 가능)
        if (Boolean.TRUE.equals(wantRemove)) {
            // 기본이미지로 전환 (정책에 따라 null 저장도 가능)
            user.setProfileUrl(defaultProfileUrlOrNull());
            // 이전 S3 삭제 (기본이미지는 삭제 제외)
            safeDeleteOldIfNeeded(oldUrl);
        }

        // 2) 파일 업로드가 오면 교체
        if (newFile != null && !newFile.isEmpty()) {
            String uploadedUrl = s3Service.uploadFile(newFile); // 공개 URL 반환한다고 가정
            user.setProfileUrl(uploadedUrl);
            // 이전 S3 삭제 (단, 이전이 기본이미지면 삭제 안 함)
            safeDeleteOldIfNeeded(oldUrl);
        }

        // 3) (선택) URL 직접 세팅이 필요하다면 유지
        setIfHasText(req.getProfileUrl(), user::setProfileUrl);

        return userRepository.save(user);
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
    }

    /* ===================== 유틸 ===================== */

    private interface Setter<T> { void set(T v); }
    private static void setIfHasText(String value, Setter<String> setter) {
        if (StringUtils.hasText(value)) setter.set(value);
    }

    private String defaultProfileUrlOrNull() {
        // 정책 A: return null;  // 뷰/프론트에서 null이면 기본이미지로 fallback
        // 정책 B: 기본 URL 고정 저장(여기선 B)
        return defaultProfileUrl;
    }

    private boolean isDefaultUrl(String url) {
        if (!StringUtils.hasText(url) || !StringUtils.hasText(defaultProfileUrl)) return false;
        // 같은 도메인이 아닐 수 있으므로, endsWith 비교 정도로 완화
        return url.endsWith("/" + defaultProfileUrl.replaceFirst("^/+", ""));
    }

    private void safeDeleteOldIfNeeded(String oldUrl) {
        if (!StringUtils.hasText(oldUrl)) return;
        if (isDefaultUrl(oldUrl)) return; // 기본이미지는 삭제 대상 아님(로컬/정적 리소스일 가능성)
        try {
            // S3Service에 "URL기반 삭제" 기능이 없다면, URL→Key 변환 유틸을 S3Service 쪽에 구현하세요.
            s3Service.deleteFileByUrl(oldUrl);
        } catch (Exception e) {
            // 베스트 에포트: 실패해도 로깅만. 주기 GC가 최종 정리.
            // log.warn("S3 old image delete failed: {}", oldUrl, e);
        }
    }
}
