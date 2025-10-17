package com.example.food_delivery_managing_system.chat.controller;

import com.example.food_delivery_managing_system.chat.dto.ChatUserDto;
import com.example.food_delivery_managing_system.user.entity.CustomUserDetails;
import com.example.food_delivery_managing_system.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatViewController {
    // ChatService로부터 데이터를 받아오지 않고, 유저 정보만 넘긴 뒤, 브라우저에서 api를 통해 데이터 사용
    @GetMapping
    public String getChatListView(Model model, @AuthenticationPrincipal CustomUserDetails authUser) {
        User user = authUser.getUser();
        ChatUserDto currentUser = ChatUserDto.builder()
                .userId(user.getUserId())
                .nickname(user.getNickName())
                .profileImageUrl(user.getProfileUrl())
                .build();

        model.addAttribute("currentUser", currentUser);
        return "chat/responsiveChat";
    }

//    @GetMapping("/{chatId}")
//    public String getChatView(@PathVariable("chatId") Long chatId, Model model, @AuthenticationPrincipal User user) {
//        ChatUserDto currentUser = ChatUserDto.builder()
//                .userId(user.getUserId())
//                .nickname(user.getNickName())
//                .profileImageUrl(user.getProfileUrl())
//                .build();
//        model.addAttribute("chatId", chatId);
//        model.addAttribute("currentUser", currentUser);
//        return "/chat/original_chat";
//    }
}
