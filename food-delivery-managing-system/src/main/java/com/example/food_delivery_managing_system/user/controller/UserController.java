package com.example.food_delivery_managing_system.user.controller;

import com.example.food_delivery_managing_system.menu.dto.MenuSummaryResponse;
import com.example.food_delivery_managing_system.restaurant.RestaurantService;
import com.example.food_delivery_managing_system.restaurant.dto.RestaurantListResponse;
import com.example.food_delivery_managing_system.user.dto.UserResponse;
import com.example.food_delivery_managing_system.user.service.UserService;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RestaurantService restaurantService;

    @GetMapping("/check-email")
    public boolean checkEmail(@RequestParam String email) {
        return userService.existsByEmail(email);
    }

    @GetMapping("/nick-name")
    public boolean checkNickName(@RequestParam String nickName) {
        return userService.existsByNickName(nickName);
    }

    @GetMapping("/position")
    public UserResponse getUserPosition(Principal principal) {
        String userEmail = principal.getName();
        return userService.getUserDetails(userEmail);
    }

    @GetMapping("/restaurants")
    public List<RestaurantListResponse> getListOfRestaurants(Principal principal) {
        String userEmail = principal.getName();
        return restaurantService.getListOfRestaurants(userEmail);
    }

    @GetMapping("/restaurants/{restaurantIdx}")
    public List<MenuSummaryResponse> getListOfMenu(@PathVariable Long restaurantIdx) {
        return userService.getListOfMenus(restaurantIdx);
    }
}
