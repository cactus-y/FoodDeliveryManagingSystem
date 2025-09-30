package com.example.food_delivery_managing_system.RestaurantLike;

import com.example.food_delivery_managing_system.Restaurant.Restaurant;
import com.example.food_delivery_managing_system.Restaurant.RestaurantRepository;
import com.example.food_delivery_managing_system.RestaurantLike.dto.LikeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final RestaurantRepository restaurantRepository;

    // POST: 특정 식당 좋아요
    public LikeResponse like(Long restaurantId, Long userId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        // User user = userRepository.findById(userId) ...
        Like like = new Like();
        like.setRestaurant(restaurant);
        // like.setUser(user);
        // TODO: User 테이블과의 연계 후 like.setUser(user);
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
