package com.example.food_delivery_managing_system.chat.dto;

import com.example.food_delivery_managing_system.chat.domain.Message;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

// 채팅방 목록 조회 출력 DTO, 사용자를 기준으로 사용자가 속한 모든 채팅방을 출력, 채팅 목록 화면을 위한 것
// 채팅방 id, 채팅 상대 객체 리스트, 마지막 메시지, 마지막 메시지 보낸 날짜 (마지막 메시지 기준으로 정렬에 활용)
@Getter
public class ChatResponse {
    private Long chatId;
    private List<ChatUserDto> chatUsers;
    private String chatTitle;
    private String lastMessage;
    private LocalDateTime lastMessageSentAt;
    @Setter
    private Long unreadCount;

    @Builder
    public ChatResponse(Long chatId, List<ChatUserDto> chatUsers, String chatTitle, String lastMessage, LocalDateTime lastMessageSentAt, Long unreadCount) {
        this.chatId = chatId;
        this.chatUsers = chatUsers;
        this.chatTitle = chatTitle;
        this.lastMessage = lastMessage;
        this.lastMessageSentAt = lastMessageSentAt;
        this.unreadCount = unreadCount;
    }
}
