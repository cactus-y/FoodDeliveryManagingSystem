package com.example.food_delivery_managing_system.chat.repository;

import com.example.food_delivery_managing_system.chat.domain.Message;
import com.example.food_delivery_managing_system.chat.dto.ChatMessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // 특정 채팅방의 메시지 조회 (최신순)
//    @Query("SELECT new com.example.food_delivery_managing_system.chat.dto.MessageResponse(" +
//            "m.messageId, m.content, m.createdAt, " +
//            "m.sender.id, m.sender.nickname, m.chat.chatId) " +
//            "FROM Message m " +
//            "WHERE m.chat.chatId = :chatRoomId " +
//            "ORDER BY m.createdAt DESC")
//    List<MessageResponse> findMessagesByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    // 특정 채팅방 모든 메시지 조회
    @Query("SELECT new com.example.food_delivery_managing_system.chat.dto.ChatMessageResponse(" +
           "    m.chat.chatId, " +
           "    new com.example.food_delivery_managing_system.chat.dto.ChatUserDto(" +
           "        m.sender.userId, m.sender.nickName, m.sender.profileUrl" +
           "    ), " +
           "    m.content, " +
           "    m.createdAt" +
           ") " +
           "FROM Message m " +
           "WHERE m.chat.chatId = :chatId " +
           "ORDER BY m.createdAt ASC")
    List<ChatMessageResponse> findAllMessagesByChatId(@Param("chatId") Long chatId);

    // 여러 채팅방의 마지막 메시지 한 번에 조회
    @Query("SELECT m FROM Message m JOIN FETCH m.chat c " +
            "WHERE m.messageId IN (" +
            "    SELECT MAX(sub_m.messageId) FROM Message sub_m WHERE sub_m.chat.chatId IN :chatIds GROUP BY sub_m.chat.chatId" +
            ")")
    List<Message> findLastMessagesByChatIds(@Param("chatIds") List<Long> chatIds);


    // 채팅방의 모든 메시지 삭제
    @Modifying
    @Query("DELETE FROM Message m WHERE m.chat.chatId = :chatId")
    void deleteAllByChatId(@Param("chatId") Long chatId);

    // 특정 시간 이후 생성된 메시지 세기
    @Query("SELECT count(m) FROM Message m " +
           "WHERE m.chat.chatId = :chatId AND m.createdAt > :lastReadAt")
    Long countUnreadMessagesAfter(@Param("chatId") Long chatId, @Param("lastReadAt") LocalDateTime lastReadAt);
}
