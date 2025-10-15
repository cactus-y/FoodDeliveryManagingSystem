package com.example.food_delivery_managing_system.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CreateChatRequest {
    private List<Long> participantIds;
    private String initialMessage;

    @Builder
    public CreateChatRequest(List<Long> participantIds, String initialMessage) {
        this.participantIds = participantIds;
        this.initialMessage = initialMessage;
    }
}