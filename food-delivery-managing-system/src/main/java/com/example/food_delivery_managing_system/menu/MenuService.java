package com.example.food_delivery_managing_system.menu;

import com.example.food_delivery_managing_system.menu.dto.AddMenuRequest;
import com.example.food_delivery_managing_system.menu.dto.MenuResponse;
import com.example.food_delivery_managing_system.menu.dto.MenuSearchResponse;
import com.example.food_delivery_managing_system.menu.dto.MenuSummaryResponse;
import com.example.food_delivery_managing_system.restaurant.Restaurant;
import com.example.food_delivery_managing_system.restaurant.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;

    //식당에 메뉴 신규 등록
    public MenuResponse createMenu(Long restaurantId, AddMenuRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        Menu menu = Menu.builder()
                .name(request.getName())
                .price(request.getPrice())
                .description(request.getDescription())
                .isSignature(request.getIsSignature() != null ? request.getIsSignature() : "N")
                .imageUrl(request.getImageUrl())
//                .restaurantIdx(restaurantId)
                .restaurant(restaurant)
                .build();

        Menu saved = menuRepository.save(menu);
        return MenuResponse.builder()
                .menuIdx(saved.getMenuIdx())
//                .restaurantIdx(saved.getRestaurantIdx())//
                .restaurantIdx(saved.getRestaurant().getRestaurantIdx())
                .name(saved.getName())
                .price(saved.getPrice())
                .description(saved.getDescription())
                .isSignature(saved.getIsSignature())
                .imageUrl(saved.getImageUrl())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    //식당 메뉴 목록 조회
    public Page<MenuSummaryResponse> getMenusByRestaurant(Long restaurantId, Pageable pageable) {
        Page<Menu> menus = menuRepository.findByRestaurant_RestaurantIdx(restaurantId, pageable);

        return menus
                .map(menu -> MenuSummaryResponse.builder()
                        .menuIdx(menu.getMenuIdx())
                        .name(menu.getName())
                        .price(menu.getPrice())
                        .isSignature(menu.getIsSignature())
                        .imageUrl(menu.getImageUrl())
                        .createdAt(menu.getCreatedAt())
                        .build());
    }

    //메뉴 단건 상세 조회
    public MenuResponse getMenuById(Long id) {
        Menu menu = menuRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        return MenuResponse.builder()
                .menuIdx(menu.getMenuIdx())
                .restaurantIdx(menu.getRestaurant().getRestaurantIdx())
                .name(menu.getName())
                .price(menu.getPrice())
                .description(menu.getDescription())
                .isSignature(menu.getIsSignature())
                .imageUrl(menu.getImageUrl())
                .createdAt(menu.getCreatedAt())
                .build();
    }

    public void deleteMenuById(Long id) {
        menuRepository.deleteById(id);
    }

    //메뉴 정보 수정
    @Transactional
    public MenuResponse updateMenu(Long menuId, AddMenuRequest request) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException("메뉴 없음"));

        if (request.getName() != null) menu.setName(request.getName());
        if (request.getPrice() != null) menu.setPrice(request.getPrice());
        if (request.getDescription() != null) menu.setDescription(request.getDescription());
        if (request.getIsSignature() != null) menu.setIsSignature(request.getIsSignature());
        if (request.getImageUrl() != null) menu.setImageUrl(request.getImageUrl());

        menu.setUpdatedAt(LocalDateTime.now());

        return MenuResponse.builder()
                .menuIdx(menu.getMenuIdx())
                .restaurantIdx(menu.getRestaurant().getRestaurantIdx())
                .name(menu.getName())
                .price(menu.getPrice())
                .description(menu.getDescription())
                .isSignature(menu.getIsSignature())
                .imageUrl(menu.getImageUrl())
                .createdAt(menu.getCreatedAt())
                .updatedAt(menu.getUpdatedAt())
                .build();
    }

    //메뉴 검색
    public Page<MenuSearchResponse> searchMenus(String keyword, Pageable pageable) {
        Page<Menu> menus = menuRepository.findByNameContainingOrDescriptionContaining(keyword, keyword, pageable);

        return menus.map(menu -> MenuSearchResponse.builder()
                        .menuIdx(menu.getMenuIdx())
                        .restaurantIdx(menu.getRestaurant().getRestaurantIdx())
                        .name(menu.getName())
                        .price(menu.getPrice())
                        .isSignature(menu.getIsSignature())
                        .imageUrl(menu.getImageUrl())
                        .build());

    }

}


