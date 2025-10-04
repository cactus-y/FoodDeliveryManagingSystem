package com.example.food_delivery_managing_system.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessageResponse {
    private Long chatId;
    private ChatUserDto sender;
    private String content;
    private LocalDateTime sentAt;

    @Builder
    public ChatMessageResponse(Long chatId, ChatUserDto sender, String content, LocalDateTime sentAt) {
        this.chatId = chatId;
        this.sender = sender;
        this.content = content;
        this.sentAt = sentAt;
    }
}
