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
    List<Menu> findAllByRestaurant_RestaurantIdx(Long restaurantId);

    Page<Menu> findByNameContainingOrDescriptionContaining(
            String nameKeyword, String descKeyword, Pageable pageable);

    // 메뉴 이름 또는 설명에 keyword가 포함된 항목을 찾기
    Page<Menu> findByRestaurantAndNameContainingIgnoreCaseOrRestaurantAndDescriptionContainingIgnoreCase(
            Restaurant restaurant1, String nameKeyword,
            Restaurant restaurant2, String descKeyword,
            Pageable pageable
    );

    Optional<Menu> findFirstByRestaurantAndIsSignatureOrderByName(Restaurant restaurant, String isSignature);
}
