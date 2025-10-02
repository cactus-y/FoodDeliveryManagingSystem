package com.example.food_delivery_managing_system.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SendMessageRequest {

    private Long chatRoomId;
    private String content;

    @Builder
    public SendMessageRequest(Long chatRoomId, String content) {
        this.chatRoomId = chatRoomId;
        this.content = content;
    }
}