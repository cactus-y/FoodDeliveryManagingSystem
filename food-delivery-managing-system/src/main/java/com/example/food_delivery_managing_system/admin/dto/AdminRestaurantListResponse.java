package com.example.food_delivery_managing_system.admin.dto;

import com.example.food_delivery_managing_system.restaurant.dto.RestaurantStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AdminRestaurantListResponse {
     private Long restaurantId;
     private String restaurantName;
     private String signatureMenu; // menu entity 의 isSignature 조회 ('Y' or 'N')
     @JsonFormat(pattern = "yyyy-MM-dd")
     private LocalDateTime createdAt;
     private RestaurantStatus restaurantStatus;
     private Double latitude;
     private Double longitude;

     public AdminRestaurantListResponse(
             Long restaurantId,
             String restaurantName,
             String signatureMenu,
             LocalDateTime createdAt,
             RestaurantStatus restaurantStatus,
             Double latitude,
             Double longitude
     ) {
          this.restaurantId = restaurantId;
          this.restaurantName = restaurantName;
          this.signatureMenu = signatureMenu;
          this.createdAt = createdAt;
          this.restaurantStatus = restaurantStatus;
          this.latitude = latitude;
          this.longitude = longitude;
     }
}
