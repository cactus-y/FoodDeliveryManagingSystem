package com.example.food_delivery_managing_system.Restaurant;

import com.example.food_delivery_managing_system.Restaurant.dto.RestaurantAoMResponse;
import com.example.food_delivery_managing_system.Restaurant.dto.RestaurantDetailResponse;
import com.example.food_delivery_managing_system.Restaurant.dto.RestaurantListResponse;
import com.example.food_delivery_managing_system.RestaurantLike.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RestaurantTestPageController {
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
        return "restaurantAddOrModify";
    }

    @GetMapping("/restaurants")
    public String getRestaurantList(Model model){
        Point my = new Point(126,37);
        List<RestaurantListResponse> list = restaurantService.getListOfRestaurants(my);
        model.addAttribute("restaurants",list);
        return "restaurantList";
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
            return "restaurantDetail";
        }
    }
}
