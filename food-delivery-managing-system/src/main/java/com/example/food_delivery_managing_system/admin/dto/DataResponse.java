package com.example.food_delivery_managing_system.admin.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DataResponse {
    private String dataName;
    private Long dataCount;

    @Builder
    public DataResponse(String dataName, Long dataCount) {
        this.dataName = dataName;
        this.dataCount = dataCount;
    }
}
