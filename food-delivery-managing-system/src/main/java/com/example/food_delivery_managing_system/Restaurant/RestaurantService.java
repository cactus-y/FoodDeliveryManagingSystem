package com.example.food_delivery_managing_system.restaurant;

import com.example.food_delivery_managing_system.restaurant.dto.AddRestaurantRequest;
import com.example.food_delivery_managing_system.restaurant.dto.RestaurantListResponse;
import com.example.food_delivery_managing_system.restaurant.dto.UpdateRestaurantRequest;
import com.example.food_delivery_managing_system.user.entity.User;
import com.example.food_delivery_managing_system.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    // POST: 내 식당 추가
    public Restaurant addRestaurant(AddRestaurantRequest request) {
        User user = userRepository.findByEmail(request.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getUsername()));
        return restaurantRepository.save(request.toEntity(user));
    }

    // GET: 동네 식당 목록 조회
    public List<RestaurantListResponse> getListOfRestaurants(String myUsername) {
        Point myCoordinates = userRepository.findByEmail(myUsername)
            .orElseThrow(() -> new IllegalArgumentException("User not found with email"))
            .getCoordinates();

        return restaurantRepository.findAll()
            .stream().map(restaurant->new RestaurantListResponse(restaurant, myCoordinates, myUsername))
            .filter(response -> response.getDistance() <= 3) // 내 좌표로부터 2km 이내(추후반영)
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
            request.getCoordinates().getX(),
            request.getCoordinates().getY(),
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
