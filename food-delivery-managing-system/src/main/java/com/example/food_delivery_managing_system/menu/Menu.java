package com.example.food_delivery_managing_system.menu;

import com.example.food_delivery_managing_system.restaurant.Restaurant;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "menus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_idx")
    private Long menuIdx;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_signature", length = 1, nullable = false)
    private String isSignature; // "Y" or "N"

    @Column(name = "image_url", length = 200)
    private String imageUrl;

//    @Column(name = "restaurant_idx")
//    private Long restaurantIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_idx")
    private Restaurant restaurant;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isSignature == null) this.isSignature = "N";
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}