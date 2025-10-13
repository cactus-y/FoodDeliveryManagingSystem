package com.example.food_delivery_managing_system.chat.service;

import com.example.food_delivery_managing_system.chat.domain.*;
import com.example.food_delivery_managing_system.chat.dto.*;
import com.example.food_delivery_managing_system.chat.repository.ChatRepository;
import com.example.food_delivery_managing_system.chat.repository.MessageRepository;
import com.example.food_delivery_managing_system.chat.repository.UserChatRelationshipRepository;
import com.example.food_delivery_managing_system.user.entity.User;
import com.example.food_delivery_managing_system.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserChatRelationshipRepository relationshipRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    // 사용자가 속한 모든 채팅방 조회
    // 정렬은 서비스레이어에서 말고 조회시(DB 레벨 정렬이 더 효율적이라고 함)
    public List<ChatResponse> getChatByUserId(Long userId) {
        // 사용자가 속한 모든 채팅방 ID 리스트 저장
        List<Long> chatIds = relationshipRepository.findAllByUserIdWithChat(userId, RelationshipStatus.ACTIVE)
                .stream()
                .map(ucr -> ucr.getChat().getChatId())
                .toList();

        if (chatIds.isEmpty()) { return Collections.emptyList(); }

        // 채팅방 ID 리스트로 모든 참여자 정보를 조회 (참가자 리스트를 리턴해야 하기 때문)
        List<UserChatRelationship> allRelationships = relationshipRepository.findAllByChat_ChatIdIn(chatIds, RelationshipStatus.ACTIVE);
        Map<Long, List<ChatUserDto>> usersByChatId = allRelationships
                .stream()
                .collect(Collectors.groupingBy(
                        ucr -> ucr.getChat().getChatId(),
                        Collectors.mapping(ucr -> ChatUserDto.builder()
                                .userId(ucr.getUser().getUserId())
                                .nickname(ucr.getUser().getNickName())
                                .profileImageUrl(ucr.getUser().getProfileUrl())
                                .build(), Collectors.toList())
                ));

        // 채팅방 ID 리스트로 각 채팅방의 마지막 메시지 정보를 조회해서 Map<chatId, Message> 형태로 저장
        List<Message> lastMessages = messageRepository.findLastMessagesByChatIds(chatIds);
        Map<Long, Message> lastMessageByChatId = lastMessages
                .stream()
                .collect(Collectors.toMap(m -> m.getChat().getChatId(), m -> m));

        // 최종적으로 ChatResponseDto 리스트 생성하고 리턴
        List<ChatResponse> responses = chatIds
                .stream()
                .map(chatId -> {
                    List<ChatUserDto> chatUsers = usersByChatId.getOrDefault(chatId, Collections.emptyList());
                    Message lastMessage = lastMessageByChatId.get(chatId);

                    return ChatResponse.builder()
                            .chatId(chatId)
                            .chatUsers(chatUsers)
                            .lastMessage(lastMessage != null ? lastMessage.getContent() : null)
                            .lastMessageSentAt(lastMessage != null ? lastMessage.getCreatedAt() : null)
                            .build();
                }).toList();

        return responses;
    }

    // 채팅방 생성 (재입장 로직 포함)
    @Transactional
    public CreateChatResponse createChat(CreateChatRequest request, Long myUserId) {
        // 자기 자신 채팅 초대 방지
        if (request.getParticipantIds().contains(myUserId)) {
            throw new IllegalArgumentException("자기 자신을 초대할 수 없습니다");
        }

        // 일단 1대1 채팅방 부분만 구현. 단체 채팅방은 시간 될 때...
        if (request.getParticipantIds().size() == 1) {
            // 먼저 유저의 유효성 검사
            User me = userRepository.findById(myUserId)
                    .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 없습니다"));
            User anotherUser = userRepository.findById(request.getParticipantIds().get(0))
                    .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 없습니다"));
            List<ChatUserDto> participants = new ArrayList<>();
            participants.add(ChatUserDto.builder()
                    .userId(me.getUserId())
                    .nickname(me.getNickName())
                    .profileImageUrl(me.getProfileUrl())
                    .build());
            participants.add(ChatUserDto.builder()
                    .userId(anotherUser.getUserId())
                    .nickname(anotherUser.getNickName())
                    .profileImageUrl(anotherUser.getProfileUrl())
                    .build());

            // 중복 체팅방 채크 (ACTIVE가 한 명이라도 있으면 생성하지 않고 기존 채팅방 리턴)
            Optional<Long> existingChatId = relationshipRepository.findActiveOneToOneChatIdByUsers(anotherUser.getUserId(), myUserId, RelationshipStatus.ACTIVE);
            if (existingChatId.isPresent()) {
                Chat existingChat = chatRepository.findById(existingChatId.get())
                        .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다!"));
                // 중복 채팅방이 존재할 경우 이미 존재하는 채팅방 리턴
                return CreateChatResponse.builder()
                        .chat(existingChat)
                        .participants(participants)
                        .message(messageRepository.save(Message.builder()
                                .chat(existingChat)
                                .sender(me)
                                .content(request.getInitialMessage())
                                .build()))
                        .build();
            }

            // 중복 채팅방이 없기 때문에 새로 생성
            Chat newChat = chatRepository.save(new Chat());
            return CreateChatResponse.builder()
                    .chat(newChat)
                    .participants(participants)
                    .message(messageRepository.save(Message.builder()
                            .chat(newChat)
                            .sender(me)
                            .content(request.getInitialMessage())
                            .build()))
                    .build();
        } else return null;
    }
        // 중복 채팅방 체크 (ACTIVE, LEFT 모두 포함)
//        Optional<Long> existingChatRoomId = relationshipRepository.findExistingChatRoomId(myUserId, opponentUserId);
//
//        if (existingChatRoomId.isPresent()) {
//            // 기존 채팅방이 있을 때
//            Long chatRoomId = existingChatRoomId.get();
//
//            // 채팅방 조회, 영속성 컨택스트 관리?
//            Chat chat = chatRepository.findById(chatRoomId)
//                    .orElseThrow(() -> new IllegalStateException("채팅방 조회 실패"));
//
//            // 내 관계 조회
//            Optional<UserChatRelationship> myRelationshipOpt = relationshipRepository.findByUserIdAndChatId(myUserId, chatRoomId);
//
//            if (myRelationshipOpt.isPresent()) {
//                UserChatRelationship myRelationship = myRelationshipOpt.get();
//
//                // LEFT 상태였으면 ACTIVE로 복구
//                if (myRelationship.getStatus() == RelationshipStatus.LEFT) {
//                    myRelationship.rejoin();
//
//                    // 재입장 알림 메시지 생성
//                    User myUser = userRepository.findById(myUserId)
//                            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
//
//                    Message systemMessage = Message.builder()
//                            .content(myUser.getNickname() + "님이 채팅방에 다시 참여했습니다.")
//                            .sender(null)
//                            .chat(chat)
//                            .build();
//                    messageRepository.save(systemMessage);
//
//                    chat.updateLastMessageTime();
//                    chatRepository.save(chat); // 명시적
//                }
//            }
//
//            return chatRepository.findChatRoomResponseById(chatRoomId, opponentUserId)
//                    .orElseThrow(() -> new IllegalStateException("채팅방 조회 실패"));
//    }

//        // 새 채팅방 생성
//        Chat newChat = new Chat();
//        Chat savedChat = chatRepository.save(newChat);
//
//        // User 엔티티 조회
//        User myUser = userRepository.findById(myUserId)
//                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
//
//        User opponentUser = userRepository.findById(opponentUserId)
//                .orElseThrow(() -> new IllegalArgumentException("상대방을 찾을 수 없습니다"));
//
//        // UserChatRelationship 생성
//        UserChatRelationship myRelationship = UserChatRelationship.builder()
//                .user(myUser)
//                .chat(savedChat)
//                .status(RelationshipStatus.ACTIVE)
//                .build();
//
//        UserChatRelationship opponentRelationship = UserChatRelationship.builder()
//                .user(opponentUser)
//                .chat(savedChat)
//                .status(RelationshipStatus.ACTIVE)
//                .build();
//
//        relationshipRepository.saveAll(List.of(myRelationship, opponentRelationship));
//
//        // 응답 생성
//        return new ChatResponse(
//                savedChat.getChatId(),
//                savedChat.getCreatedAt(),
//                savedChat.getUpdatedAt(),
//                new OpponentUser(
//                        opponentUser.getId(),
//                        opponentUser.getNickname(),
//                        opponentUser.getProfileImageUrl()
//                ),
//                null
//        );
//    }

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
        List<UserChatRelationship> allRelationships = relationshipRepository.findAllByChatId(chatRoomId);

        // 모두 LEFT 상태인지 확인
        boolean allLeft = allRelationships.stream()
                .allMatch(rel -> rel.getStatus() == RelationshipStatus.LEFT);

        if (allLeft) {
            // 모두 나갔으면 채팅방 및 관련 데이터 삭제
            // 메시지 삭제
            messageRepository.deleteAllByChatId(chatRoomId);
            // UserChatRelationship 삭제
            relationshipRepository.deleteAllByChatId(chatRoomId);
            // Chat 삭제
            chatRepository.deleteById(chatRoomId);
        }
    }

    // 채팅 저장
    @Transactional
    public ChatMessageResponse saveMessage(ChatMessageRequest request) {
        // 유저 유효성 검사
        User sender = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다"));
        // 채팅방 유효성 검사
        Chat currentChat = chatRepository.findById(request.getChatId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다"));
        // 저장
        Message savedMessage = messageRepository.save(Message.builder()
                .chat(currentChat)
                .sender(sender)
                .content(request.getContent())
                .build());

        return ChatMessageResponse.builder()
                .chatId(request.getChatId())
                .sender(ChatUserDto.builder()
                        .userId(sender.getUserId())
                        .nickname(sender.getNickName())
                        .profileImageUrl(sender.getProfileUrl()).build())
                .content(savedMessage.getContent())
                .sentAt(savedMessage.getCreatedAt())
                .build();
    }

    public List<ChatMessageResponse> findMessagesByChatId(Long chatId) {
        return messageRepository.findAllMessagesByChatId(chatId);
    }
}

// 예외처리는 예외 파일 하나 따로 만들어서 하기(나중에)
// 채팅부분 sql 인젝션은 어떻게 방지할까? -> JPA에서 알아서 보호해 준대요! template의 XSS만 막으면 웬만하면 안전할 듯 합니다.