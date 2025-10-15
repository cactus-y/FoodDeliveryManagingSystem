package com.example.food_delivery_managing_system.restaurantLike;

import com.example.food_delivery_managing_system.restaurant.Restaurant;
import com.example.food_delivery_managing_system.restaurant.RestaurantRepository;
import com.example.food_delivery_managing_system.restaurantLike.dto.LikeResponse;
import com.example.food_delivery_managing_system.user.entity.User;
import com.example.food_delivery_managing_system.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    // POST: 특정 식당 좋아요
    public LikeResponse like(Long restaurantId, String myUsername) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        User user = userRepository.findByEmail(myUsername).get();
        Like like = new Like();
        like.setRestaurant(restaurant);
        like.setUser(user);
        return new LikeResponse(likeRepository.save(like));
    }

    // GET: 좋아요 여부 조회
    public Like getLiked(Long restaurantId, String myUsername){
        try{
            Like like = likeRepository.findAll()
                    .stream().filter(L ->
                            L.getRestaurant().getRestaurantIdx().equals(restaurantId)
                            &&
                            L.getUser().getEmail().equals(myUsername)
                    )
                    .toList().get(0);
            return like;
        } catch (ArrayIndexOutOfBoundsException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // DELETE: 특정 식당 좋아요 취소
    public void unLike(Long restaurantId, String myUsername){
        try{
            Like like = likeRepository.findAll()
                    .stream().filter(L ->
                            L.getRestaurant().getRestaurantIdx().equals(restaurantId)
                            &&
                            L.getUser().getEmail().equals(myUsername)
                    )
                    .toList().get(0);
            likeRepository.deleteById(like.getLikeIdx());
        } catch (ArrayIndexOutOfBoundsException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
