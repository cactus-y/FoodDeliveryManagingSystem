package com.example.food_delivery_managing_system.restaurant;

import com.example.food_delivery_managing_system.restaurant.dto.RestaurantAoMResponse;
import com.example.food_delivery_managing_system.restaurant.dto.RestaurantDetailResponse;
import com.example.food_delivery_managing_system.restaurant.dto.RestaurantListResponse;
import com.example.food_delivery_managing_system.restaurantLike.LikeService;
import com.example.food_delivery_managing_system.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class RestaurantPageController {
    private final RestaurantService restaurantService;
    private final LikeService likeService;
    private final UserRepository userRepository;

    @GetMapping("/restaurantsAddOrModify")
    public String addOrModifyRestaurant(@RequestParam(required = false) Long restaurantId, Model model, Principal principal) {
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
    public String getRestaurantList(Model model, Principal principal) {
        Point myCoordinates = userRepository.findByEmail(principal.getName()).get().getCoordinates();
        // Point my = new GeometryFactory().createPoint(new Coordinate(126,37)); // 임시 좌표값
        List<RestaurantListResponse> list = restaurantService.getListOfRestaurants(myCoordinates);
        model.addAttribute("restaurants",list);
        return "restaurant/restaurantList";
    }

    @GetMapping("/restaurants/{restaurantId}")
    public String getRestaurantDetail(@PathVariable Long restaurantId, Model model, Principal principal){
        Long myUserId = userRepository.findByEmail(principal.getName()).get().getUserId();
        // Long userId = 1L; // 임시 userId
        // TODO: 헤더나 파라미터로부터 내 userId값을 받아옴
        boolean liked = false;
        try{
            if(likeService.getLiked(restaurantId, myUserId) != null) liked = true;
        } catch(ArrayIndexOutOfBoundsException e) {
            liked = false;
        } finally {
            Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
            model.addAttribute("restaurant",new RestaurantDetailResponse(restaurant, liked));
            return "restaurant/restaurantDetail";
        }
    }
}
