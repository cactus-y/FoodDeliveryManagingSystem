package com.example.food_delivery_managing_system.restaurantLike;

import com.example.food_delivery_managing_system.restaurantLike.dto.LikeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restaurants/{restaurantId}/likes")
public class LikeController {
    private final LikeService likeService;

    // POST: 특정 식당 좋아요
    @PostMapping
    public LikeResponse like(@PathVariable Long restaurantId, Principal principal){
        String myUsername = principal.getName();
        // TODO: 헤더나 파라미터로부터 내 userId값 받아오기
        return likeService.like(restaurantId, myUsername);
    }

    // GET: 좋아요 여부 조회
    @GetMapping
    public ResponseEntity<LikeResponse> getLiked(@PathVariable Long restaurantId, Principal principal){
        String myUsername = principal.getName();
        Like like = likeService.getLiked(restaurantId, myUsername);
        LikeResponse response = new LikeResponse(like);
        return ResponseEntity.ok(response);
    }

    // DELETE: 특정 식당 좋아요 취소
    @DeleteMapping
    public ResponseEntity<Void> unlike(@PathVariable Long restaurantId, Principal principal){
        String myUsername = principal.getName();
        likeService.unLike(restaurantId, myUsername);
        return ResponseEntity.ok().build();
    }
}