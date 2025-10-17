package com.example.food_delivery_managing_system.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageRequest {
    private Long chatId;
    private Long userId;
    private String content;

    @Builder
    public ChatMessageRequest(Long chatId, Long userId, String content) {
        this.chatId = chatId;
        this.userId = userId;
        this.content = content;
    }
}