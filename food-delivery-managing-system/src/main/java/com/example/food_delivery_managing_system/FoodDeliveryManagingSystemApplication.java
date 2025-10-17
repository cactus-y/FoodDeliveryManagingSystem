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
    @PostConstruct
    public void printEnv() {
        System.out.println("[ENV] RDS_URL=" + System.getenv("RDS_URL"));
        System.out.println("[ENV] RDS_USERNAME=" + System.getenv("RDS_USERNAME"));
        String pw = System.getenv("RDS_PASSWORD");
        System.out.println("[ENV] RDS_PASSWORD length=" + (pw == null ? "null" : pw.length()));
    }
}
