package com.example.food_delivery_managing_system.menu;

import com.example.food_delivery_managing_system.restaurant.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu,Long> {
    Page<Menu> findByRestaurant_RestaurantIdx(Long restaurantId, Pageable pageable);
    Page<Menu> findByNameContainingOrDescriptionContaining(
            String nameKeyword, String descKeyword, Pageable pageable);

    Optional<Menu> findFirstByRestaurantAndIsSignatureOrderByName(Restaurant restaurant, String isSignature);
}
