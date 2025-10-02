package com.example.food_delivery_managing_system.menu.dto;

import com.example.food_delivery_managing_system.menu.Menu;
import lombok.*;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddMenuRequest {
    private String name;
    private BigDecimal price;
    private String description;
    private String isSignature;
    private String imageUrl;

}
