package com.example.food_delivery_managing_system;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FoodDeliveryManagingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodDeliveryManagingSystemApplication.class, args);
    }
}