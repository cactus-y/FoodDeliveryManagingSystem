package com.example.food_delivery_managing_system.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserPatchRequest {

    // ===== 문자열 필드 =====
    private String nickName;
    private String roadAddress;
    private String detailAddress;

    // ===== 좌표 =====
    private Double latitude;
    private Double longitude;
    private Point coordinates; // 서버 내부 변환용

    // ===== 프로필 이미지 =====
    private MultipartFile profileImage;   // 새로 업로드할 이미지 파일
    private Boolean removeProfileImage;   // 이미지 삭제 시 true (백엔드에서 default 처리 가능)

    private String profileUrl; // 직접 URL 지정이 필요한 경우 (일반적으로는 S3 업로드 후 설정)
}
