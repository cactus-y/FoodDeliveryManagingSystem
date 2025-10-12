package com.example.food_delivery_managing_system.chat.controller;

import com.example.food_delivery_managing_system.chat.dto.*;
import com.example.food_delivery_managing_system.chat.service.ChatService;
import com.example.food_delivery_managing_system.user.eneity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatController {
    private final ChatService chatService;
    private final SimpMessageSendingOperations messagingTemplate;

    // 채팅방 목록 조회
    @GetMapping("/me/chats")
    public ResponseEntity<List<ChatResponse>> getChats(@PathVariable @AuthenticationPrincipal User user) {
        List<ChatResponse> chatRooms = chatService.getChatByUserId(user.getUserId());
        return ResponseEntity.ok(chatRooms);
    }

    // 채팅방 생성
    @PostMapping("/chats")
    public ResponseEntity<ChatResponse> createChat(@RequestBody CreateChatRequest request, @AuthenticationPrincipal User user) {
        CreateChatResponse response = chatService.createChat(request, user.getUserId());
        // 초기 메시지를 보내는 사람은 채팅방을 만든 사람이기 때문에 creator는 본인
        ChatUserDto creator = ChatUserDto.builder()
                .userId(user.getUserId())
                .nickname(user.getNickName())
                .profileImageUrl(user.getProfileUrl())
                .build();

        // 초기 메시지 저장 후 전파
        messagingTemplate.convertAndSend("/sub/chats/" + response.getChat().getChatId(), ChatMessageResponse.builder()
                .chatId(response.getChat().getChatId())
                .sender(creator)
                .content(response.getMessage().getContent())
                .sentAt(response.getMessage().getCreatedAt())
                .build()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(ChatResponse.builder()
                .chatId(response.getChat().getChatId())
                .chatUsers(response.getParticipants())
                .lastMessage(response.getMessage().getContent())
                .lastMessageSentAt(response.getMessage().getCreatedAt())
                .build()
        );
    }
    // 채팅방 나가기
    @DeleteMapping("/chats/{chatId}")
    public ResponseEntity<Void> leaveChatRoom(@PathVariable Long chatId, @AuthenticationPrincipal User user) {
        chatService.leaveChatRoom(user.getUserId(), chatId);
        return ResponseEntity.noContent().build();
    }

    // 채팅방 접속했을 때 이전 메시지 불러오기
    @GetMapping("/chats/{chatId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(@PathVariable Long chatId) {
        return ResponseEntity.ok(chatService.findMessagesByChatId(chatId));
    }


    // ===== Security 적용 후 사용할 API =====

/*
    @GetMapping("/me/chats")
    public ResponseEntity<List<ChatRoomResponse>> getChatRooms(
            @AuthenticationPrincipal Long userId) {

        List<ChatRoomResponse> chatRooms = chatService.getChatRoomsByUserId(userId);
        return ResponseEntity.ok(chatRooms);
    }

    @PostMapping("/chats")
    public ResponseEntity<ChatRoomResponse> createChatRoom(
            @AuthenticationPrincipal Long userId,
            @RequestBody CreateChatRoomRequest request) {

        ChatRoomResponse response = chatService.createChatRoom(userId, request.getOpponentUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/chats/{chatRoomId}")
    public ResponseEntity<Void> leaveChatRoomWithAuth(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long chatRoomId) {

        chatService.leaveChatRoom(userId, chatRoomId);
        return ResponseEntity.noContent().build();
    }
*/

}
