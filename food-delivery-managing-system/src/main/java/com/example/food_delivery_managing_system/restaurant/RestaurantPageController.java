package com.example.food_delivery_managing_system.restaurant;

import com.example.food_delivery_managing_system.menu.dto.MenuSummaryResponse;
import com.example.food_delivery_managing_system.restaurant.dto.RestaurantAoMResponse;
import com.example.food_delivery_managing_system.restaurant.dto.RestaurantDetailResponse;
import com.example.food_delivery_managing_system.restaurant.dto.RestaurantListResponse;
import com.example.food_delivery_managing_system.restaurantLike.LikeService;
import com.example.food_delivery_managing_system.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    public String getRestaurantList(
            Model model,
            Principal principal,
            @PageableDefault(size = 10, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable
    ) {
        String myUsername = principal.getName();

        Page<RestaurantListResponse> page = restaurantService.getPagesOfRestaurants(myUsername, pageable);

        int blockSize = 5; // 페이지가 많을 시 번호를 5개씩 보여줌
        int current = page.getNumber();
        int start = (current / blockSize) * blockSize;
        int end = Math.min(start + blockSize, page.getTotalPages() - 1);

        model.addAttribute("start", start);
        model.addAttribute("end", end);

        model.addAttribute("page", page);

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
