package com.example.food_delivery_managing_system.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null 필드는 안 들어오게(선택)
public class UserPatchRequest {
    private String nickName;
    private String roadAddress;
    private String detailAddress;
    private String coordinates;
    private String profileUrl;
}
