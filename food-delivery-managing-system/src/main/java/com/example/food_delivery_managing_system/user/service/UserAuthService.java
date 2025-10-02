package com.example.food_delivery_managing_system.user.service;

import com.example.food_delivery_managing_system.user.repository.UserRepository;
import com.example.food_delivery_managing_system.user.dto.LoginRequestDto;
import com.example.food_delivery_managing_system.user.dto.LoginResponseDto;
import com.example.food_delivery_managing_system.user.eneity.CustomUserDetails;
import com.example.food_delivery_managing_system.user.eneity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

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

        // 여기서 principal 사용
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        User user = userRepository.findById(principal.getId()).orElseThrow();

        return LoginResponseDto.from(user);
    }


    public void logout(HttpServletRequest httpRequest) {
        SecurityContextHolder.clearContext();
        HttpSession session = httpRequest.getSession(false);
        if (session != null) session.invalidate();
    }
}
