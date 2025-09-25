package com.example.food_delivery_managing_system.RestaurantLike;

import com.example.food_delivery_managing_system.Restaurant.Restaurant;
import com.example.food_delivery_managing_system.Restaurant.RestaurantRepository;
import com.example.food_delivery_managing_system.RestaurantLike.dto.LikeResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final RestaurantRepository restaurantRepository;

    // POST: 특정 식당 최초 좋아요
    public LikeResponse like(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        Like like = new Like();
        like.setRestaurant(restaurant);
        return new LikeResponse(likeRepository.save(like));
    }

    // DELETE: 특정 식당 좋아요 상태 변경
    public void unLike(Long id){
        likeRepository.deleteById(id);
    }
}
