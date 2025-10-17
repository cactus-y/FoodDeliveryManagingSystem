package com.example.food_delivery_managing_system.chat.controller;

import com.example.food_delivery_managing_system.chat.dto.ChatMessageRequest;
import com.example.food_delivery_managing_system.chat.service.ChatService;
import com.example.food_delivery_managing_system.user.entity.CustomUserDetails;
import com.example.food_delivery_managing_system.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatSocketController {
    private final ChatService chatService;

//    // /pub/chats/messages 를 통해 들어오는 메시지를 받아서 송출하는 역할
//    @MessageMapping("/chats/messages")
//    public void sendMessage(@Payload ChatMessageRequest request, @AuthenticationPrincipal CustomUserDetails authUser) {
//        User user = authUser.getUser();
//        // 1대1 채팅방 한정: 이미 떠났던 채팅방에서 메시지를 받았으면 다시 채팅방 활성화 시켜야 함
//        chatService.sendMessage(request, user.getUserId());
//    }

    // /pub/chats/messages 를 통해 들어오는 메시지를 받아서 송출하는 역할
    @MessageMapping("/chats/messages")
    public void sendMessage(@Payload ChatMessageRequest request, Principal principal) {
        if (principal == null) {
            throw new AccessDeniedException("인증되지 않은 사용자 입니다");
        }

        CustomUserDetails userDetails = (CustomUserDetails) ((Authentication) principal).getPrincipal();

        User user = userDetails.getUser();
        // 1대1 채팅방 한정: 이미 떠났던 채팅방에서 메시지를 받았으면 다시 채팅방 활성화 시켜야 함
        chatService.sendMessage(request, user.getUserId());
    }
}
