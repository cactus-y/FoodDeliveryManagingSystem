package com.example.food_delivery_managing_system.chat.repository;

import com.example.food_delivery_managing_system.chat.domain.RelationshipStatus;
import com.example.food_delivery_managing_system.chat.domain.UserChatRelationship;
import com.example.food_delivery_managing_system.chat.dto.ChatResponse;
import com.example.food_delivery_managing_system.chat.dto.ChatUserDto;
import com.example.food_delivery_managing_system.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserChatRelationshipRepository extends JpaRepository<UserChatRelationship, Long> {
    // JPQL Constructor Expression 사용
    // join 이 많지만 DB 자체는 1회만 조회하기 때문에 효율적
    // 대신 데이터으 양이 많아지면 인덱싱 필수!

    // 채팅방의 모든 관계 삭제
    @Modifying
    @Query("DELETE FROM UserChatRelationship ucr WHERE ucr.chat.chatId = :chatId")
    void deleteAllByChatId(@Param("chatId") Long chatId);

    // 특정 사용자의 특정 채팅방 관계 조회
    @Query("SELECT ucr FROM UserChatRelationship ucr " +
            "WHERE ucr.user.userId = :userId " +
            "AND ucr.chat.chatId = :chatId")
    Optional<UserChatRelationship> findByUserIdAndChatId(
            @Param("userId") Long userId,
            @Param("chatId") Long chatId);

    // 채팅방의 모든 관계 조회
    @Query("SELECT ucr FROM UserChatRelationship ucr " +
            "JOIN FETCH ucr.user " +
            "WHERE ucr.chat.chatId = :chatId")
    List<UserChatRelationship> findAllByChatId(@Param("chatId") Long chatId);


    // 특정 유저가 속한 모든 관계 조회
    @Query("SELECT ucr FROM UserChatRelationship ucr " +
           "JOIN FETCH ucr.chat c " +
           "WHERE ucr.user.userId = :userId AND ucr.status = :status")
    List<UserChatRelationship> findAllByUserIdWithChat(@Param("userId") Long userId,
                                                       @Param("status") RelationshipStatus status);

    // 특정 채팅방의 모든 관계 조회
    @Query("SELECT ucr FROM UserChatRelationship ucr " +
           "JOIN FETCH ucr.user u " +
           "JOIN FETCH ucr.chat c " +
           "WHERE ucr.chat.chatId IN :chatIds")
    List<UserChatRelationship> findAllByChat_ChatIdIn(@Param("chatIds") List<Long> chatIds);

    // 1대1 채팅방 한정: 둘 중 하나라도 ACTIVE한 채팅방 조회
    @Query("SELECT ucr1.chat.chatId " +
           "FROM UserChatRelationship ucr1 " +
           "JOIN UserChatRelationship ucr2 ON ucr1.chat.chatId = ucr2.chat.chatId " +
           "WHERE ucr1.user.userId = :userId1 " +
           "AND ucr2.user.userId = :userId2 " +
           "AND (ucr1.status = :status OR ucr1.status = :status) " +
           // 이 아래 쿼리는 해당 채팅방이 1대1 채팅방인 것을 확인하는 절차
           "AND ucr1.chat.chatId IN (" +
           "    SELECT c.chat.chatId FROM UserChatRelationship c " +
           "    GROUP BY c.chat.chatId " +
           "    HAVING COUNT(c.chat.chatId) = 2" +
           ")")
    Optional<Long> findActiveOneToOneChatIdByUsers(@Param("userId1") Long userId1,
                                                   @Param("userId2") Long userId2,
                                                   @Param("status") RelationshipStatus status);
}
