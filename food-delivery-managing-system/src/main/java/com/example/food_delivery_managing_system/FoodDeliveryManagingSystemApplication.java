package com.example.food_delivery_managing_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FoodDeliveryManagingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodDeliveryManagingSystemApplication.class, args);
	}

}
