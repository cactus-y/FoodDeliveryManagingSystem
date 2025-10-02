package com.example.food_delivery_managing_system.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuSearchResponse {
    private Long menuIdx;
    private Long restaurantIdx;
    private String name;
    private BigDecimal price;
    private String isSignature;
    private String imageUrl;
}
