package com.example.food_delivery_managing_system.user.controller;

import com.example.food_delivery_managing_system.user.service.UserAuthService;
import com.example.food_delivery_managing_system.user.dto.LoginRequestDto;
import com.example.food_delivery_managing_system.user.dto.LoginResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAuthController {

    private final UserAuthService userAuthService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto,
                                                  HttpServletRequest httpRequest) {
        LoginResponseDto response = userAuthService.login(requestDto, httpRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest httpRequest) {
        userAuthService.logout(httpRequest);
        return ResponseEntity.ok().body("{\"message\":\"logout success\"}");
    }
}
