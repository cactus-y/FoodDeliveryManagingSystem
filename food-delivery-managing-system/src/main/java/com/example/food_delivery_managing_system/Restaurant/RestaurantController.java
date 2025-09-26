package com.example.food_delivery_managing_system.Restaurant;

import com.example.food_delivery_managing_system.Restaurant.dto.AddRestaurantRequest;
import com.example.food_delivery_managing_system.Restaurant.dto.RestaurantDetailResponse;
import com.example.food_delivery_managing_system.Restaurant.dto.RestaurantListResponse;
import com.example.food_delivery_managing_system.Restaurant.dto.UpdateRestaurantRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restaurants")
public class RestaurantController {
    private final RestaurantService restaurantService;

    // POST: 내 식당 추가
    @PostMapping
    public ResponseEntity<Restaurant> addRestaurant(@RequestBody AddRestaurantRequest request) {
        Restaurant addRestaurant = restaurantService.addRestaurant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(addRestaurant);
    }

    // GET: 동네 식당 목록 조회
    @GetMapping
    public ResponseEntity<List<RestaurantListResponse>> getListOfRestaurants(/**/) {
        Point my = new Point(126,37); // 임시 좌표값
        // TODO: 헤더나 파리미터로부터 내 좌표값을 받아와서 my에 저장
        List<RestaurantListResponse> list = restaurantService.getListOfRestaurants(my);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    // GET: 특정 식당 정보 조회
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDetailResponse> getRestaurantById(@PathVariable Long id) {
        Restaurant restaurant = restaurantService.getRestaurantById(id);
        RestaurantDetailResponse response = new RestaurantDetailResponse(restaurant);
        return ResponseEntity.ok(response);
    }

    // PUT: 내 식당 수정
    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(@PathVariable Long id, @RequestBody UpdateRestaurantRequest request) {
        Restaurant updateRestaurant = restaurantService.updateRestaurant(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(updateRestaurant);
    }

    // DELETE: 내 식당 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurantById(@PathVariable Long id) {
        restaurantService.deleteRestaurantById(id);
        return ResponseEntity.ok().build();
    }
}
