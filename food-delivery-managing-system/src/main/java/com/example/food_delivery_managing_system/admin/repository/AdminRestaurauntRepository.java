package com.example.food_delivery_managing_system.admin.repository;

import com.example.food_delivery_managing_system.admin.dto.PostListResponse;
import com.example.food_delivery_managing_system.restaurant.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AdminRestaurauntRepository extends JpaRepository<Restaurant, Long> {
    @Query("SELECT r "
            + "FROM Restaurant r "
            + "ORDER BY r.createdAt DESC")
    List<Restaurant> findAllRestaurantsOrderByCreatedAt();
}
