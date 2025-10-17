package com.example.food_delivery_managing_system.restaurant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findByUserEmailAndName(String email, String name);

    @Query("""
           SELECT r FROM Restaurant r
           LEFT JOIN FETCH r.user
           LEFT JOIN FETCH r.likes
           WHERE r.restaurantIdx = :id
           """)
    Optional<Restaurant> findByIdWithUserAndLikes(@Param("id") Long id);

    @Query("SELECT r FROM Restaurant r")
    Page<Restaurant> findAllWithPaging(Pageable pageable);
}
