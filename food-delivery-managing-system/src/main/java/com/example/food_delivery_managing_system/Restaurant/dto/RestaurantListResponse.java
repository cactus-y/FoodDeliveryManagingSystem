package com.example.food_delivery_managing_system.Restaurant.dto;

import com.example.food_delivery_managing_system.Restaurant.Restaurant;
import lombok.Getter;
import org.springframework.data.geo.Point;

import java.time.LocalDateTime;

@Getter
public class RestaurantListResponse {
    private Long id;
    private String name;
    private Point coordinates;
    private Double distance;
    private LocalDateTime createdAt;
    private String openAt;
    private String closeAt;
    private String imageUrl;
    private Float restaurantRating;

    public RestaurantListResponse(Restaurant restaurant, Point my) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.coordinates = new Point(restaurant.getCoordinates());
        this.distance = distanceOfTwoPoints(restaurant.getCoordinates(), my);
        this.createdAt = restaurant.getCreatedAt();
        this.openAt = restaurant.getOpenAt();
        this.closeAt = restaurant.getCloseAt();
        this.imageUrl = restaurant.getImageUrl();
        this.restaurantRating = restaurant.getRestaurantRating();
    }

    public Double distanceOfTwoPoints(Point a, Point b){
        double x1 = Math.toRadians(a.getX());
        double y1 = Math.toRadians(a.getY());
        double x2 = Math.toRadians(b.getX());
        double y2 = Math.toRadians(b.getY());

        double dlon = x2 - x1;
        double dlat = y2 - y1;

        double x = Math.pow(Math.sin(dlat/2), 2) + Math.cos(y1) * Math.cos(y2) * Math.pow(Math.sin(dlon/2), 2);
        double c = 2 * Math.atan2(Math.sqrt(x), Math.sqrt(1-x));

        double radius = 6371; // 지구 반지름(km)

        return radius * c;
    }
}
