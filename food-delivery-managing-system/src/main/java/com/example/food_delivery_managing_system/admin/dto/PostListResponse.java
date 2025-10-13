package com.example.food_delivery_managing_system.admin.dto;

import com.example.food_delivery_managing_system.restaurant.dto.RestaurantStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.locationtech.jts.geom.Point;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostListResponse {
     private String restaurantName;
     private String signatureMenu; // menu entity 의 isSignature 조회 ('Y' or 'N')
     @JsonFormat(pattern = "yyyy-MM-dd")
     private LocalDateTime createdAt;
     private RestaurantStatus restaurantStatus;
     private Point coordinates;

     public PostListResponse(
             String restaurantName,
             String signatureMenu,
             LocalDateTime createdAt,
             RestaurantStatus restaurantStatus,
             Point coordinates) {
          this.restaurantName = restaurantName;
          this.signatureMenu = signatureMenu;
          this.createdAt = createdAt;
          this.restaurantStatus = restaurantStatus;
          this.coordinates = coordinates;
     }
}
