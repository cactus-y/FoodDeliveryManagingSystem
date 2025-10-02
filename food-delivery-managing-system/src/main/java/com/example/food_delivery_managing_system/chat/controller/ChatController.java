package com.example.food_delivery_managing_system.chat.controller;

import com.example.food_delivery_managing_system.chat.dto.ChatRoomResponse;
import com.example.food_delivery_managing_system.chat.dto.CreateChatRoomRequest;
import com.example.food_delivery_managing_system.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatController {
    private final ChatService chatService;

    // 채팅방 목록 조회 - PathVariable 사용. 테스트용
    @GetMapping("/me/{userId}/chats")
    public ResponseEntity<List<ChatRoomResponse>> getChatRoomsByUser(@PathVariable Long userId) {
        List<ChatRoomResponse> chatRooms = chatService.getChatRoomsByUserId(userId);
        return ResponseEntity.ok(chatRooms);
    }

    // 채팅방 생성 -  PathVariable (테스트용)
    @PostMapping("/{userId}/chats")
    public ResponseEntity<ChatRoomResponse> createChatRoomWithPath(
            @PathVariable Long userId,
            @RequestBody CreateChatRoomRequest request) {

        ChatRoomResponse response = chatService.createChatRoom(userId, request.getOpponentUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    // 채팅방 나가기 - (PathVariable - 테스트용)
    @DeleteMapping("/users/{userId}/rooms/{chatRoomId}")
    public ResponseEntity<Void> leaveChatRoom(
            @PathVariable Long userId,
            @PathVariable Long chatRoomId) {

        chatService.leaveChatRoom(userId, chatRoomId);
        return ResponseEntity.noContent().build();
    }


    // ===== Security 적용 후 사용할 API =====

/*
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getChatRooms(
            @AuthenticationPrincipal Long userId) {

        List<ChatRoomResponse> chatRooms = chatService.getChatRoomsByUserId(userId);
        return ResponseEntity.ok(chatRooms);
    }

    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomResponse> createChatRoom(
            @AuthenticationPrincipal Long userId,
            @RequestBody CreateChatRoomRequest request) {

        ChatRoomResponse response = chatService.createChatRoom(userId, request.getOpponentUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/rooms/{chatRoomId}")
    public ResponseEntity<Void> leaveChatRoomWithAuth(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long chatRoomId) {

        chatService.leaveChatRoom(userId, chatRoomId);
        return ResponseEntity.noContent().build();
    }
*/

}
