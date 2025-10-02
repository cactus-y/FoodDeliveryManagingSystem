package com.example.food_delivery_managing_system.chat.service;

import com.example.food_delivery_managing_system.chat.domain.Chat;
import com.example.food_delivery_managing_system.chat.domain.Message;
import com.example.food_delivery_managing_system.chat.domain.RelationshipStatus;
import com.example.food_delivery_managing_system.chat.domain.User;
import com.example.food_delivery_managing_system.chat.dto.MessageResponse;
import com.example.food_delivery_managing_system.chat.dto.SendMessageRequest;
import com.example.food_delivery_managing_system.chat.repository.ChatRepository;
import com.example.food_delivery_managing_system.chat.repository.MessageRepository;
import com.example.food_delivery_managing_system.chat.repository.UserChatRelationshipRepository;
import com.example.food_delivery_managing_system.chat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final UserChatRelationshipRepository relationshipRepository;

    @Transactional
    public MessageResponse sendMessage(Long senderId, SendMessageRequest request) {

        // 메시지 존재 확인
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("메시지 내용은 필수입니다");
        }

        if (request.getChatRoomId() == null) {
            throw new IllegalArgumentException("채팅방 ID는 필수입니다");
        }

        // 채팅방 있는지 확인
        Chat chat = chatRepository.findById(request.getChatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다"));

        // 발신자가 채팅방 멤버 + ACTIVE 상태인지 확인
        boolean isActiveMember = relationshipRepository
                .existsActiveMember(senderId, request.getChatRoomId());

        if (!isActiveMember) {
            throw new IllegalArgumentException("채팅방에 참여하지 않았거나 나간 상태입니다");
        }

        // 발신자 찾기
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 메시지 생성, 저장
        Message message = Message.builder()
                .content(request.getContent().trim())
                .sender(sender)
                .chat(chat)
                .build();

        Message savedMessage = messageRepository.save(message);

        chat.updateLastMessageTime();
        chatRepository.save(chat);  // 명시적으로 저장

        return MessageResponse.builder()
                .message(savedMessage)
                .build();
    }

    // 채팅방의 메시지 목록 조회
    @Transactional(readOnly = true)
    public List<MessageResponse> getMessagesByChatRoomId(Long chatRoomId, Long userId) {

        boolean isMember = relationshipRepository.isMemberOfChatRoom(userId, chatRoomId);
        if (!isMember) { // 채팅방 안에 있는 사용자가 접근하면
            throw new IllegalArgumentException("채팅방에 접근할 수 없습니다");
        }
        return messageRepository.findMessagesByChatRoomId(chatRoomId);
    }

}
