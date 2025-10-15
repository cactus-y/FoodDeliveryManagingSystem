package com.example.food_delivery_managing_system.chat.domain;

import com.example.food_delivery_managing_system.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class UserChatRelationship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userChatRelationshipId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Setter
    @Enumerated(EnumType.STRING)
    private RelationshipStatus status;

    private LocalDateTime lastReadAt;

    @Builder
    public UserChatRelationship(User user, Chat chat, RelationshipStatus status) {
        this.user = user;
        this.chat = chat;
        this.status = status;
        updateLastReadAt();
    }

    // 마지막으로 채팅을 읽은 시간 업데이트
    public void updateLastReadAt() {
        this.lastReadAt = LocalDateTime.now();
    }

    // 채팅방 나가기
    public void leave() {
        this.status = RelationshipStatus.LEFT;
    }

    // 채팅방 재입장
    public void rejoin() {
        this.status = RelationshipStatus.ACTIVE;
    }
}
