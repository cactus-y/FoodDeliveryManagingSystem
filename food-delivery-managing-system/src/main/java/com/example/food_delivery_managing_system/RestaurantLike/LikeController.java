package com.example.food_delivery_managing_system.RestaurantLike;

import com.example.food_delivery_managing_system.RestaurantLike.dto.LikeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LikeController {
    private final LikeService likeService;

    // POST: 특정 식당 최초 좋아요
    @PostMapping("/restaurants/{restaurantId}/likes")
    public LikeResponse like(@PathVariable Long restaurantId){
        return likeService.like(restaurantId);
    }

    // DELETE: 특정 식당 좋아요 상태 변경
    @DeleteMapping("/likes/{likeId}")
    public ResponseEntity<Void> unlike(@PathVariable Long likeId){
        likeService.unLike(likeId);
        return ResponseEntity.ok().build();
    }
}
