package com.example.food_delivery_managing_system.restaurant;

import com.example.food_delivery_managing_system.restaurant.dto.AddRestaurantRequest;
import com.example.food_delivery_managing_system.restaurant.dto.UpdateRestaurantRequest;
import com.example.food_delivery_managing_system.restaurantLike.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restaurants")
public class RestaurantController {
    private final RestaurantService restaurantService;
    private final LikeService likeService;

    // POST: 내 식당 추가
    @PostMapping
    public ResponseEntity<Restaurant> addRestaurant(@RequestBody AddRestaurantRequest request) {
        Restaurant addRestaurant = restaurantService.addRestaurant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(addRestaurant);
    }
    /*
    // GET: 동네 식당 목록 조회
    @GetMapping
    public ResponseEntity<List<RestaurantListResponse>> getListOfRestaurants( Point my ) {
        Point my = new GeometryFactory().createPoint(new Coordinate(126,37)); // 임시 좌표값
        // TODO: 헤더나 파리미터로부터 내 좌표값을 받아와서 my에 저장
        List<RestaurantListResponse> list = restaurantService.getListOfRestaurants(my);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    // GET: 특정 식당 정보 조회
    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantDetailResponse> getRestaurantById(@PathVariable Long restaurantId, Long userId) {
        userId = 1L; // 임시 userId
        // TODO: 헤더나 파라미터로부터 내 userId값을 받아옴
        boolean liked = false;
        try{
            if(likeService.getLiked(restaurantId, userId) != null) liked = true;
        } finally {
            Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
            RestaurantDetailResponse response = new RestaurantDetailResponse(restaurant, liked);
            return ResponseEntity.ok(response);
        }
    }
     */

    // PUT: 내 식당 수정
    @PutMapping("/{restaurantId}")
    public ResponseEntity<Restaurant> updateRestaurant(@PathVariable Long restaurantId, @RequestBody UpdateRestaurantRequest request) {
        Restaurant updateRestaurant = restaurantService.updateRestaurant(restaurantId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updateRestaurant);
    }

    // DELETE: 내 식당 삭제
    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<Void> deleteRestaurantById(@PathVariable Long restaurantId) {
        restaurantService.deleteRestaurantById(restaurantId);
        return ResponseEntity.ok().build();
    }
}