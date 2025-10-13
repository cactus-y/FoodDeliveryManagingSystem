package com.example.food_delivery_managing_system.restaurant;

import com.example.food_delivery_managing_system.restaurant.dto.RestaurantAoMResponse;
import com.example.food_delivery_managing_system.restaurant.dto.RestaurantDetailResponse;
import com.example.food_delivery_managing_system.restaurant.dto.RestaurantListResponse;
import com.example.food_delivery_managing_system.RestaurantLike.LikeService;
import com.example.food_delivery_managing_system.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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
        String myUsername = principal.getName();
        if(restaurantId == null){
            model.addAttribute("restaurant", new RestaurantAoMResponse(myUsername));
        }
        else{
            Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
            model.addAttribute("restaurant", new RestaurantAoMResponse(restaurant, myUsername));
        }
        return "restaurant/restaurantAddOrModify";
    }

    @GetMapping("/restaurants")
    public String getRestaurantList(Model model, Principal principal) {
        String myUsername = principal.getName();
        List<RestaurantListResponse> list = restaurantService.getListOfRestaurants(myUsername);
        model.addAttribute("restaurants",list);
        return "restaurant/restaurantList";
    }

    @GetMapping("/restaurants/{restaurantId}")
    public String getRestaurantDetail(@PathVariable Long restaurantId, Model model, Principal principal){
        String myUsername = principal.getName();
        boolean liked = false;
        try{
            if(likeService.getLiked(restaurantId, myUsername) != null) liked = true;
        } catch(ArrayIndexOutOfBoundsException e) {
            liked = false;
        } finally {
            Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
            model.addAttribute("restaurant",new RestaurantDetailResponse(restaurant, myUsername, liked));
            return "restaurant/restaurantDetail";
        }
    }
}