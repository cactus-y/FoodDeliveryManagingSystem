package com.example.food_delivery_managing_system.chat.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Table(name = "chats")
@Getter
@NoArgsConstructor
@Entity
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    // 1대1일 경우 null 처리, 단체 채팅방만 이름 저장
    // 단체 채팅방 이름을 따로 정하지 않았다면 '{채팅방 만든 사람 닉네임}외 n-1명', 정했다면 정한 이름으로 사용
    @Setter
    @Column(name = "chat_title")
    private String chatTitle;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
