package com.example.food_delivery_managing_system.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Getter
@NoArgsConstructor
public class CreateChatRoomRequest {

    private Long opponentUserId;

    public CreateChatRoomRequest(Long opponentUserId) {
        this.opponentUserId = opponentUserId;
    }
}