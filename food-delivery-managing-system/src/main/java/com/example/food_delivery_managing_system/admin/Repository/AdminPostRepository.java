package com.example.food_delivery_managing_system.admin.Repository;

import com.example.food_delivery_managing_system.restaurant.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminPostRepository extends JpaRepository<Restaurant, Long>  {
}
