package com.example.food_delivery_managing_system.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuSummaryResponse {
    private Long menuIdx;
    private String name;
    private BigDecimal price;
    private String isSignature;
    private String imageUrl;
    private LocalDateTime createdAt;
}
