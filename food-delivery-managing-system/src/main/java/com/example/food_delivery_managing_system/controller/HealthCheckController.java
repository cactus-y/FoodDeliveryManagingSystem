package com.example.food_delivery_managing_system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/")
    public String root() {
        return "ok"; // "ok" 문자열과 함께 HTTP 200 응답을 반환
    }
}