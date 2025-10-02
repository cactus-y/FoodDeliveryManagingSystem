package com.example.food_delivery_managing_system.chat.service;

import com.example.food_delivery_managing_system.chat.domain.*;
import com.example.food_delivery_managing_system.chat.dto.ChatRoomResponse;
import com.example.food_delivery_managing_system.chat.dto.OpponentUser;
import com.example.food_delivery_managing_system.chat.repository.ChatRepository;
import com.example.food_delivery_managing_system.chat.repository.MessageRepository;
import com.example.food_delivery_managing_system.chat.repository.UserChatRelationshipRepository;
import com.example.food_delivery_managing_system.chat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserChatRelationshipRepository relationshipRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    // 사용자가 속한 모든 채팅방 조회
    // 정렬은 서비스레이어에서 말고 조회시(DB 레벨 정렬이 더 효율적이라고 함)
    public List<ChatRoomResponse> getChatRoomsByUserId(Long userId) {
        return relationshipRepository.findChatRoomsWithOpponentByUserId(userId);
    }

    // 채팅방 생성 (재입장 로직 포함)
    @Transactional
    public ChatRoomResponse createChatRoom(Long myUserId, Long opponentUserId) {

        // 자기 자신과 채팅 방지
        if (myUserId.equals(opponentUserId)) {
            throw new IllegalArgumentException("자기 자신과는 채팅할 수 없습니다");
        }

        // 두 사용자 존재 확인
        List<Long> userIds = List.of(myUserId, opponentUserId);
        long count = userRepository.countByIdIn(userIds);
        if (count != 2) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다");
        }

        // 중복 채팅방 체크 (ACTIVE, LEFT 모두 포함)
        Optional<Long> existingChatRoomId = relationshipRepository.findExistingChatRoomId(myUserId, opponentUserId);

        if (existingChatRoomId.isPresent()) {
            // 기존 채팅방이 있을 때
            Long chatRoomId = existingChatRoomId.get();

            // 채팅방 조회, 영속성 컨택스트 관리?
            Chat chat = chatRepository.findById(chatRoomId)
                    .orElseThrow(() -> new IllegalStateException("채팅방 조회 실패"));

            // 내 관계 조회
            Optional<UserChatRelationship> myRelationshipOpt = relationshipRepository.findByUserIdAndChatId(myUserId, chatRoomId);

            if (myRelationshipOpt.isPresent()) {
                UserChatRelationship myRelationship = myRelationshipOpt.get();

                // LEFT 상태였으면 ACTIVE로 복구
                if (myRelationship.getStatus() == RelationshipStatus.LEFT) {
                    myRelationship.rejoin();

                    // 재입장 알림 메시지 생성
                    User myUser = userRepository.findById(myUserId)
                            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

                    Message systemMessage = Message.builder()
                            .content(myUser.getNickname() + "님이 채팅방에 다시 참여했습니다.")
                            .sender(null)
                            .chat(chat)
                            .build();
                    messageRepository.save(systemMessage);

                    chat.updateLastMessageTime();
                    chatRepository.save(chat); // 명시적
                }
            }

            return chatRepository.findChatRoomResponseById(chatRoomId, opponentUserId)
                    .orElseThrow(() -> new IllegalStateException("채팅방 조회 실패"));
        }

        // 새 채팅방 생성
        Chat newChat = new Chat();
        Chat savedChat = chatRepository.save(newChat);

        // User 엔티티 조회
        User myUser = userRepository.findById(myUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        User opponentUser = userRepository.findById(opponentUserId)
                .orElseThrow(() -> new IllegalArgumentException("상대방을 찾을 수 없습니다"));

        // UserChatRelationship 생성
        UserChatRelationship myRelationship = UserChatRelationship.builder()
                .user(myUser)
                .chat(savedChat)
                .status(RelationshipStatus.ACTIVE)
                .build();

        UserChatRelationship opponentRelationship = UserChatRelationship.builder()
                .user(opponentUser)
                .chat(savedChat)
                .status(RelationshipStatus.ACTIVE)
                .build();

        relationshipRepository.saveAll(List.of(myRelationship, opponentRelationship));

        // 응답 생성
        return new ChatRoomResponse(
                savedChat.getChatId(),
                savedChat.getCreatedAt(),
                savedChat.getUpdatedAt(),
                new OpponentUser(
                        opponentUser.getId(),
                        opponentUser.getNickname(),
                        opponentUser.getProfileImageUrl()
                ),
                null
        );
    }

    // 채팅방 나가기
    @Transactional
    public void leaveChatRoom(Long userId, Long chatRoomId) {

        // 채팅방 존재 확인
        Chat chat = chatRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다"));

        // 사용자가 이 채팅방의 멤버인지 확인
        UserChatRelationship myRelationship = relationshipRepository
                .findByUserIdAndChatId(userId, chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방에 참여하지 않았습니다"));

        // 이미 나간 상태인지 확인
        if (myRelationship.getStatus() == RelationshipStatus.LEFT) {
            throw new IllegalArgumentException("이미 나간 채팅방입니다");
        }

        // 내 상태를 LEFT로 변경
        myRelationship.leave();

        // 상대방 상태 확인
        List<UserChatRelationship> allRelationships =
                relationshipRepository.findAllByChatId(chatRoomId);

        // 모두 LEFT 상태인지 확인
        boolean allLeft = allRelationships.stream()
                .allMatch(rel -> rel.getStatus() == RelationshipStatus.LEFT);

        if (allLeft) {
            // 모두 나갔으면 채팅방 및 관련 데이터 삭제
            deleteChatRoomCompletely(chatRoomId);
        }
    }

    // 채팅방 완전 삭제
    private void deleteChatRoomCompletely(Long chatRoomId) {
        // 메시지 삭제
        messageRepository.deleteAllByChatId(chatRoomId);
        // UserChatRelationship 삭제
        relationshipRepository.deleteAllByChatId(chatRoomId);
        // Chat 삭제
        chatRepository.deleteById(chatRoomId);
    }

}

// 예외처리는 예외 파일 하나 따로 만들어서 하기(나중에)
// 채팅부분 sql 인젝션은 어떻게 방지할까?