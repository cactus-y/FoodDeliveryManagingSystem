package com.example.food_delivery_managing_system.Restaurant;

import com.example.food_delivery_managing_system.RestaurantLike.Like;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.geo.Point;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurants")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id", updatable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "road_address", nullable = false)
    private String roadAddress;

    @Column(name = "detail_address", nullable = false)
    private String detailAddress;


    @Column(name = "coordinates", nullable = false)
    private Point coordinates;


    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "open_at", nullable = false)
    private String openAt;

    @Column(name = "close_at", nullable = false)
    private String closeAt;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "additional_info")
    private String additionalInfo;

    @Column(name = "restaurant_rating")
    private Float restaurantRating = 0.0f;

    @Column(name = "user_id")
    private Long userId = 1L;

    /*
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
     */

    @OneToMany(mappedBy = "restaurant")
    private List<Like> likes = new ArrayList<>();

    @Builder
    public Restaurant(
            String name,
            String roadAddress,
            String detailAddress,
            Point coordinates,
            String openAt,
            String closeAt,
            String imageUrl,
            String additionalInfo
    ) {
        this.name = name;
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
        this.coordinates = coordinates;
        this.openAt = openAt;
        this.closeAt = closeAt;
        this.imageUrl = imageUrl;
        this.additionalInfo = additionalInfo;
    }

    public void updateRestaurant(
            String name,
            String roadAddress,
            String detailAddress,
            Point coordinates,
            String openAt,
            String closeAt,
            String imageUrl,
            String additionalInfo
    ) {
        this.name = name;
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
        this.coordinates = coordinates;
        this.openAt = openAt;
        this.closeAt = closeAt;
        this.imageUrl = imageUrl;
        this.additionalInfo = additionalInfo;
    }


}
