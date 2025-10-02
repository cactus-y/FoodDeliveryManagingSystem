package com.example.food_delivery_managing_system.chat.repository;

import com.example.food_delivery_managing_system.chat.domain.Message;
import com.example.food_delivery_managing_system.chat.dto.MessageResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // 특정 채팅방의 메시지 조회 (최신순)
    @Query("SELECT new com.example.food_delivery_managing_system.chat.dto.MessageResponse(" +
            "m.messageId, m.content, m.createdAt, " +
            "m.sender.id, m.sender.nickname, m.chat.chatId) " +
            "FROM Message m " +
            "WHERE m.chat.chatId = :chatRoomId " +
            "ORDER BY m.createdAt DESC")
    List<MessageResponse> findMessagesByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    // 채팅방의 마지막 메시지 조회
    @Query("SELECT m FROM Message m " +
            "WHERE m.chat.chatId = :chatRoomId " +
            "ORDER BY m.createdAt DESC " +
            "LIMIT 1")
    Optional<Message> findLastMessageByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    // 채팅방의 모든 메시지 삭제
    @Modifying
    @Query("DELETE FROM Message m WHERE m.chat.chatId = :chatId")
    void deleteAllByChatId(@Param("chatId") Long chatId);
}
