package com.example.food_delivery_managing_system.admin.repository;

import com.example.food_delivery_managing_system.restaurant.Restaurant;
import com.example.food_delivery_managing_system.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRestaurantRepository extends JpaRepository<Restaurant, Long> {
    @Query("SELECT r FROM Restaurant r ORDER BY r.createdAt DESC")
    List<Restaurant> findAllRestaurantsOrderByCreatedAt();

    List<Restaurant> findByUser(User user);
}
