package com.example.food_delivery_managing_system.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RelationshipStatus {
    ACTIVE(0, "Active"),
    LEFT(1, "Left");

    private int id;
    private String description;
}