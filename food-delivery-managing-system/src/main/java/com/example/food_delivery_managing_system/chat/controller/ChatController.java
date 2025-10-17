package com.example.food_delivery_managing_system.chat.controller;

import com.example.food_delivery_managing_system.chat.dto.*;
import com.example.food_delivery_managing_system.chat.service.ChatService;
import com.example.food_delivery_managing_system.user.entity.CustomUserDetails;
import com.example.food_delivery_managing_system.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatController {
    private final ChatService chatService;


    // 채팅방 목록 조회
    @GetMapping("/me/chats")
    public ResponseEntity<List<ChatResponse>> getChats(@AuthenticationPrincipal CustomUserDetails authUser) {
        User user = authUser.getUser();
        List<ChatResponse> chatRooms = chatService.getChatByUserId(user.getUserId());
        return ResponseEntity.ok(chatRooms);
    }

    // 채팅방 생성
    @PostMapping("/chats")
    public ResponseEntity<ChatResponse> createChat(@RequestBody CreateChatRequest request, @AuthenticationPrincipal CustomUserDetails authUser) {
        User user = authUser.getUser();
        ChatResponse response = chatService.createChat(request, user.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    // 채팅방 나가기
    @DeleteMapping("/chats/{chatId}")
    public ResponseEntity<Void> leaveChatRoom(@PathVariable Long chatId, @AuthenticationPrincipal CustomUserDetails authUser) {
        User user = authUser.getUser();
        chatService.leaveChatRoom(user.getUserId(), chatId);
        return ResponseEntity.noContent().build();
    }

    // 채팅방 접속했을 때 이전 메시지 불러오기
    @GetMapping("/chats/{chatId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(@PathVariable Long chatId, @AuthenticationPrincipal CustomUserDetails authUser) {
        User user = authUser.getUser();
        return ResponseEntity.ok(chatService.findMessagesByChatId(chatId, user.getUserId()));
    }

    // 채팅 읽음
    @PostMapping("/chats/{chatId}/read")
    public ResponseEntity<Void> readMessage(@PathVariable Long chatId, @AuthenticationPrincipal CustomUserDetails authUser) {
        User user = authUser.getUser();
        chatService.updateLastReadAt(chatId, user.getUserId());
        return ResponseEntity.ok().build();
    }

    // 채팅방 참가자 확인
    @GetMapping("/chats/{chatId}/participants")
    public ResponseEntity<List<ChatUserDto>> getParticipants(@PathVariable Long chatId, @AuthenticationPrincipal CustomUserDetails authUser) {
        User user = authUser.getUser();
        return ResponseEntity.ok(chatService.getParticipants(chatId, user.getUserId()));
    }
}
