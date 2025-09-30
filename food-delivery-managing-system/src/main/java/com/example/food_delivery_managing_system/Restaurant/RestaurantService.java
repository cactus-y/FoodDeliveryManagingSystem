package com.example.food_delivery_managing_system.Restaurant;

import com.example.food_delivery_managing_system.Restaurant.dto.AddRestaurantRequest;
import com.example.food_delivery_managing_system.Restaurant.dto.RestaurantListResponse;
import com.example.food_delivery_managing_system.Restaurant.dto.UpdateRestaurantRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

    // POST: 내 식당 추가
    public Restaurant addRestaurant(AddRestaurantRequest request, Long userId) {
        // User user = userRepository.findById(userId);
        return restaurantRepository.save(request.toEntity(/* user */));
    }

    // GET: 동네 식당 목록 조회
    public List<RestaurantListResponse> getListOfRestaurants(Point my) {
        return restaurantRepository.findAll()
                .stream().map(restaurant->new RestaurantListResponse(restaurant, my))
                // .filter(response -> response.getDistance() <= 2) // 내 좌표로부터 2km 이내(추후반영)
                .toList();
    }

    // GET: 특정 식당 정보 조회
    public Restaurant getRestaurantById(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // PUT: 내 식당 수정
    @Transactional
    public Restaurant updateRestaurant(Long id, UpdateRestaurantRequest request) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        restaurant.updateRestaurant(
                request.getName(),
                request.getRoadAddress(),
                request.getDetailAddress(),
                request.getCoordinates(),
                request.getOpenAt(),
                request.getCloseAt(),
                request.getImageUrl(),
                request.getAdditionalInfo()
        );
        return restaurant;
    }

    // DELETE: 내 식당 삭제
    public void deleteRestaurantById(Long id) {
        restaurantRepository.deleteById(id);
    }
}
