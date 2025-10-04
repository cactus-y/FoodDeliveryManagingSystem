package com.example.food_delivery_managing_system.chat.repository;

import com.example.food_delivery_managing_system.chat.domain.RelationshipStatus;
import com.example.food_delivery_managing_system.chat.domain.UserChatRelationship;
import com.example.food_delivery_managing_system.chat.dto.ChatResponse;
import com.example.food_delivery_managing_system.chat.dto.ChatUserDto;
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
//    @Query("SELECT new com.example.food_delivery_managing_system.chat.dto.ChatResponse(" +
//            "c.chatId, " +
//            "c.createdAt, " +
//            "c.updatedAt, " +
//            "new com.example.food_delivery_managing_system.chat.dto.OpponentUser(" +
//            "    opponent.id, opponent.nickname, opponent.profileImageUrl" +
//            "), " +
//            "(SELECT m.content FROM Message m " +  // 서브쿼리로 최신 메시지 조회
//            " WHERE m.chat.chatId = c.chatId " +
//            " ORDER BY m.createdAt DESC LIMIT 1)) " +
//            "FROM UserChatRelationship ucr " +
//            "JOIN ucr.chat c " +
//            "JOIN UserChatRelationship opponentRel ON opponentRel.chat.chatId = c.chatId " +
//            "JOIN opponentRel.user opponent " +
//            "WHERE ucr.user.id = :userId " + // 사용자가 속한 채팅방 중에
//            "AND ucr.status = 'ACTIVE' " + // 사용자가 들어가 있는 방만
//            "AND opponent.id != :userId " + // 상대방이 내가 아닌 방들만 조회
//            "ORDER BY c.updatedAt DESC") // 가장 최근 업데이트 부터 내림차순
//    List<ChatResponse> findChatRoomsWithOpponentByUserId(@Param("userId") Long userId);
//
//    // 중복 채팅방 체크
//    @Query("SELECT ucr1.chat.chatId FROM UserChatRelationship ucr1 " +
//            "JOIN UserChatRelationship ucr2 ON ucr1.chat.chatId = ucr2.chat.chatId " +
//            "WHERE ucr1.user.id = :userId " +
//            "AND ucr2.user.id = :opponentUserId")
//    Optional<Long> findExistingChatRoomId(
//            @Param("userId") Long userId,
//            @Param("opponentUserId") Long opponentUserId);
//
//    // 사용자가 채팅방 멤버이며 ACTIVE 상태인지 확인. 채팅방 나갈 때 채팅방 삭제 여부를 판단하기 위함
//    @Query("SELECT COUNT(ucr) > 0 FROM UserChatRelationship ucr " +
//            "WHERE ucr.user.id = :userId " +
//            "AND ucr.chat.chatId = :chatRoomId " +
//            "AND ucr.status = 'ACTIVE'")
//    boolean existsActiveMember(@Param("userId") Long userId, @Param("chatRoomId") Long chatRoomId);
//
//    // 채팅방의 메시지 조회 권한 확인
//    @Query("SELECT COUNT(ucr) > 0 FROM UserChatRelationship ucr " +
//            "WHERE ucr.user.id = :userId " +
//            "AND ucr.chat.chatId = :chatRoomId")
//    boolean isMemberOfChatRoom(@Param("userId") Long userId, @Param("chatRoomId") Long chatRoomId);

    // 채팅방의 모든 관계 삭제
    @Modifying
    @Query("DELETE FROM UserChatRelationship ucr WHERE ucr.chat.chatId = :chatId")
    void deleteAllByChatId(@Param("chatId") Long chatId);

    // 특정 사용자의 특정 채팅방 관계 조회
    @Query("SELECT ucr FROM UserChatRelationship ucr " +
            "WHERE ucr.user.id = :userId " +
            "AND ucr.chat.chatId = :chatId")
    Optional<UserChatRelationship> findByUserIdAndChatId(
            @Param("userId") Long userId,
            @Param("chatId") Long chatId);

    // 채팅방의 모든 관계 조회
    @Query("SELECT ucr FROM UserChatRelationship ucr " +
            "WHERE ucr.chat.chatId = :chatId")
    List<UserChatRelationship> findAllByChatId(@Param("chatId") Long chatId);


    // 특정 유저가 속한 모든 관계 조회
    @Query("SELECT ucr FROM UserChatRelationship ucr " +
           "JOIN FETCH ucr.chat c " +
           "WHERE ucr.user.id = :userId AND ucr.status = :status")
    List<UserChatRelationship> findAllByUserIdWithChat(@Param("userId") Long userId,
                                                       @Param("status") RelationshipStatus status);

    // 특정 채팅방의 모든 관계 조회
    @Query("SELECT ucr FROM UserChatRelationship ucr " +
           "JOIN FETCH ucr.user u " +
           "JOIN FETCH ucr.chat c " +
           "WHERE ucr.chat.chatId IN :chatIds AND ucr.status = :status")
    List<UserChatRelationship> findAllByChat_ChatIdIn(@Param("chatIds") List<Long> chatIds,
                                                      @Param("status") RelationshipStatus status);

    // 1대1 채팅방 한정: 둘 중 하나라도 ACTIVE한 채팅방 조회
    @Query("SELECT ucr1.chat.chatId " +
           "FROM UserChatRelationship ucr1 " +
           "JOIN UserChatRelationship ucr2 ON ucr1.chat.chatId = ucr2.chat.chatId " +
           "WHERE ucr1.user.id = :userId1 " +
           "AND ucr2.user.id = :userId2 " +
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
