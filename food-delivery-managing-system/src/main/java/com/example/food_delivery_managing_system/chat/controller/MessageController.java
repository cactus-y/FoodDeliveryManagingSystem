package com.example.food_delivery_managing_system.chat.controller;

import com.example.food_delivery_managing_system.chat.dto.MessageResponse;
import com.example.food_delivery_managing_system.chat.dto.SendMessageRequest;
import com.example.food_delivery_managing_system.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // 메시지 저장 (테스트용)
    @PostMapping("/{userId}")
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable Long userId,
            @RequestBody SendMessageRequest request) {

        MessageResponse response = messageService.sendMessage(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 채팅방 메시지 목록 조회 (테스트용)
    @GetMapping("/{userId}/rooms/{chatRoomId}")
    public ResponseEntity<List<MessageResponse>> getMessages(
            @PathVariable Long userId,
            @PathVariable Long chatRoomId) {

        List<MessageResponse> messages = messageService.getMessagesByChatRoomId(chatRoomId, userId);
        return ResponseEntity.ok(messages);
    }

    /*

        // Security 적용 후
    @PostMapping("/messages")
    public ResponseEntity<MessageResponse> sendMessageWithAuth(
            @AuthenticationPrincipal Long userId,
            @RequestBody SendMessageRequest request) {

        MessageResponse response = messageService.sendMessage(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/rooms/{chatRoomId}/messages")
    public ResponseEntity<List<MessageResponse>> getMessagesWithAuth(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long chatRoomId) {

        List<MessageResponse> messages = messageService.getMessagesByChatRoomId(chatRoomId, userId);
        return ResponseEntity.ok(messages);
    }

    * */
}