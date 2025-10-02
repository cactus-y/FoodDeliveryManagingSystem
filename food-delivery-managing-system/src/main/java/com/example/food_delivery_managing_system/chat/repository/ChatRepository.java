package com.example.food_delivery_managing_system.chat.repository;

import com.example.food_delivery_managing_system.chat.domain.Chat;
import com.example.food_delivery_managing_system.chat.dto.ChatRoomResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    // 채팅방 정보 조회
    @Query("SELECT new com.example.food_delivery_managing_system.chat.dto.ChatRoomResponse(" +
            "c.chatId, " +
            "c.createdAt, " +
            "c.updatedAt, " +  // 마지막 업데이트 시간
            "new com.example.food_delivery_managing_system.chat.dto.OpponentUser(" +
            "    opponent.id, opponent.nickname, opponent.profileImageUrl" +
            "), " +
            "(SELECT m.content FROM Message m " +
            " WHERE m.chat.chatId = c.chatId " +
            " ORDER BY m.createdAt DESC LIMIT 1)) " +
            "FROM Chat c " +
            "JOIN UserChatRelationship ucr ON ucr.chat.chatId = c.chatId " +
            "JOIN ucr.user opponent " +
            "WHERE c.chatId = :chatId " +
            "AND opponent.id = :opponentUserId")
    Optional<ChatRoomResponse> findChatRoomResponseById(
            @Param("chatId") Long chatId,
            @Param("opponentUserId") Long opponentUserId);
}
