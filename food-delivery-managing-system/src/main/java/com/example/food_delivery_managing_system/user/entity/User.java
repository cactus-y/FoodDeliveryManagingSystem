package com.example.food_delivery_managing_system.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import lombok.*;
import org.locationtech.jts.geom.Point;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_idx", nullable = false, updatable = false)
    private Long userId;

    @Setter(AccessLevel.NONE)
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Setter(AccessLevel.NONE)
    @Column(name = "name", nullable = false)
    private String name;

    @Setter(AccessLevel.NONE)
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole userRole;

    @Column(name = "nick_name")
    private String nickName;

    @Column(name = "road_address")
    private String roadAddress;

    @Column(name = "detail_address")
    private String detailAddress;

    @Column(columnDefinition = "geography(Point,4326)")
    private Point coordinates;

    @Setter(AccessLevel.NONE)
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "profile_image_url")
    private String profileUrl;

    @Builder
    public User (Long userId
        , String email
        , String password
        , String name
        , UserRole userRole
        , String nickName
        , String roadAddress
        , String detailAddress
        , Point coordinates
        , LocalDateTime updatedAt
        , String profileUrl) {

        this.userId = userId;
        this.email = email;
        this.password = password;
        this.name = name;
        this.userRole = userRole == null ? UserRole.OWNER : userRole;
        this.nickName = nickName;
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
        this.coordinates = coordinates;
        this.updatedAt = updatedAt;
        this.profileUrl = profileUrl;
    }


}
