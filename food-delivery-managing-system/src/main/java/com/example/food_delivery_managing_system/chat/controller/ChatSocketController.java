package com.example.food_delivery_managing_system.chat.controller;

import com.example.food_delivery_managing_system.chat.dto.ChatMessageRequest;
import com.example.food_delivery_managing_system.chat.dto.ChatMessageResponse;
import com.example.food_delivery_managing_system.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatSocketController {
    private final ChatService chatService;
    private final SimpMessageSendingOperations messagingTemplate;

    // /pub/chats/messages 를 통해 들어오는 메시지를 받아서 송출하는 역할
    @MessageMapping("/chats/messages")
    public void sendMessage(@Payload ChatMessageRequest request) {
        // 1대1 채팅방 한정: 이미 떠났던 채팅방에서 메시지를 받았으면 다시 채팅방 활성화 시켜야 함
        ChatMessageResponse savedMessage = chatService.saveMessage(request);
        String url = "/sub/chats/" + savedMessage.getChatId();
        messagingTemplate.convertAndSend(url, savedMessage);
    }
}
