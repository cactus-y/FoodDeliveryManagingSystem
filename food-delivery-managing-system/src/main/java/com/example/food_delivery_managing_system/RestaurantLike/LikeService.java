package com.example.food_delivery_managing_system.RestaurantLike;

import com.example.food_delivery_managing_system.Restaurant.Restaurant;
import com.example.food_delivery_managing_system.Restaurant.RestaurantRepository;
import com.example.food_delivery_managing_system.RestaurantLike.dto.LikeResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final RestaurantRepository restaurantRepository;

    // POST: 특정 식당 좋아요
    public LikeResponse like(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        Like like = new Like();
        like.setRestaurant(restaurant);
        // like.setUser(user);
        return new LikeResponse(likeRepository.save(like));
    }

    // GET: 좋아요 여부 조회
    public Like getLiked(Long restaurantId, Long userId){
        try{
            Like like = likeRepository.findAll()
                    .stream().filter(L ->
                            L.getRestaurant().getId().equals(restaurantId)
                            &&
                            // L.getUser().getId().equals(userId)
                            L.getUserId().equals(userId)
                    )
                    .toList().get(0);
            return like;
        } catch (ArrayIndexOutOfBoundsException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // DELETE: 특정 식당 좋아요 취소
    public void unLike(Long restaurantId, Long userId){
        try{
            Like like = likeRepository.findAll()
                    .stream().filter(L ->
                            L.getRestaurant().getId().equals(restaurantId)
                                    &&
                                    // L.getUser().getId().equals(userId)
                                    L.getUserId().equals(userId)
                    )
                    .toList().get(0);
            likeRepository.deleteById(like.getId());
        } catch (ArrayIndexOutOfBoundsException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
