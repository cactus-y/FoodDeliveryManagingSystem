package com.example.food_delivery_managing_system.chat.dto;

import com.example.food_delivery_managing_system.chat.domain.Message;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MessageResponse {
    // 자바에서는 camelCase를 사용하지만,JSON 데이터에서는 "message_id"이라는 키를 사용하도록 지정합니다.
    // 결론적으로는 직렬화 역직렬화 시 데이터 id 명시
    // @JsonProperty("message_id")
    private Long messageId;
    private String content;
    private LocalDateTime createdAt;
    private Long senderId;
    private String senderNickname;
    private Long chatRoomId;

    @Builder
    public MessageResponse(Message message) {
        this.messageId = message.getMessageId();
        this.content = message.getContent();
        this.createdAt = message.getCreatedAt();
        this.senderId = message.getSender().getId();
        this.senderNickname = message.getSender().getNickname();
        this.chatRoomId = message.getChat().getChatId();
    }

    public MessageResponse(Long messageId, String content, LocalDateTime createdAt,
                           Long senderId, String senderNickname, Long chatRoomId) {
        this.messageId = messageId;
        this.content = content;
        this.createdAt = createdAt;
        this.senderId = senderId;
        this.senderNickname = senderNickname;
        this.chatRoomId = chatRoomId;
    }
}