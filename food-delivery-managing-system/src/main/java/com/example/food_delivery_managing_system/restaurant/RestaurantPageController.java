package com.example.food_delivery_managing_system.restaurant;

import com.example.food_delivery_managing_system.restaurant.dto.AddRestaurantRequest;
import com.example.food_delivery_managing_system.restaurant.dto.RestaurantAoMResponse;
import com.example.food_delivery_managing_system.restaurant.dto.RestaurantDetailResponse;
import com.example.food_delivery_managing_system.restaurant.dto.RestaurantListResponse;
import com.example.food_delivery_managing_system.restaurantLike.LikeService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RestaurantPageController {
    private final RestaurantService restaurantService;
    private final LikeService likeService;

    @GetMapping("/restaurantsAddOrModify")
    public String addOrModifyRestaurant(@RequestParam(required = false) Long restaurantId, Model model) {
        if(restaurantId == null){
            model.addAttribute("restaurant", new RestaurantAoMResponse());
        }
        else{
            Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
            model.addAttribute("restaurant", new RestaurantAoMResponse(restaurant));
        }
        return "restaurant/restaurantAddOrModify";
    }

    @GetMapping("/restaurants")
    public String getRestaurantList(Model model){
        Point my = new GeometryFactory().createPoint(new Coordinate(126,37)); // 임시 좌표값
        List<RestaurantListResponse> list = restaurantService.getListOfRestaurants(my);
        model.addAttribute("restaurants",list);
        return "restaurant/restaurantList";
    }

    @GetMapping("/restaurants/{restaurantId}")
    public String getRestaurantDetail(@PathVariable Long restaurantId, Model model){
        Long userId = 1L; // 임시 userId
        // TODO: 헤더나 파라미터로부터 내 userId값을 받아옴
        boolean liked = false;
        try{
            if(likeService.getLiked(restaurantId, userId) != null) liked = true;
        } catch(ArrayIndexOutOfBoundsException e) {
            liked = false;
        } finally {
            Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
            model.addAttribute("restaurant",new RestaurantDetailResponse(restaurant, liked));
            return "restaurant/restaurantDetail";
        }
    }
}
