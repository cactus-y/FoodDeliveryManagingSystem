package com.example.food_delivery_managing_system.chat.dto;

import lombok.Getter;

import java.time.LocalDateTime;

// 채팅방 목록 조회 출력 DTO, 사용자를 기준으로 사용자가 속한 모든 채팅방을 출력, 채팅 목록 화면을 위한 것
// 채팅방 id, 생성일, 채팅 상대 객체, 사용자 id, 사용자 이름, 사용자 프로필 이미지 링크, 마지막 메시지
@Getter
public class ChatRoomResponse {
    private Long chatRoomId;
    private LocalDateTime createdAt;
    private String lastMessage;
    private OpponentUser opponentUser; // 상대방 유저 정보. userId, nickname, profileImageUrl
    private LocalDateTime updatedAt;

    // JPQL Constructor Expression 생성자
    public ChatRoomResponse(Long chatRoomId, LocalDateTime createdAt, LocalDateTime updatedAt,
                            OpponentUser opponentUser, String lastMessage) {
        this.chatRoomId = chatRoomId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.opponentUser = opponentUser;
        this.lastMessage = lastMessage;
    }
}
