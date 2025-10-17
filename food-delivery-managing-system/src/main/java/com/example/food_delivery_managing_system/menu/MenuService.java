package com.example.food_delivery_managing_system.menu;

import com.example.food_delivery_managing_system.S3.S3Service;
import com.example.food_delivery_managing_system.exception.MenuNotFoundException;
import com.example.food_delivery_managing_system.exception.RestaurantNotFoundException;
import com.example.food_delivery_managing_system.menu.dto.AddMenuRequest;
import com.example.food_delivery_managing_system.menu.dto.MenuResponse;
import com.example.food_delivery_managing_system.menu.dto.MenuSearchResponse;
import com.example.food_delivery_managing_system.menu.dto.MenuSummaryResponse;
import com.example.food_delivery_managing_system.restaurant.Restaurant;
import com.example.food_delivery_managing_system.restaurant.RestaurantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;
    private final S3Service s3Service;

    //식당에 메뉴 신규 등록
    public MenuResponse createMenu(Long restaurantId, AddMenuRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

//                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

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

    //오버로딩: 페이지 컨트롤러용 메서드 (식당 내 검색 지원을 위해)
    public Page<MenuSummaryResponse> getMenusByRestaurant(Long restaurantId, String keyword, Pageable pageable) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        Page<Menu> menus;

        if (keyword == null || keyword.trim().isEmpty()) {
            // 검색어가 없으면: 전체 메뉴 조회
            menus = menuRepository.findByRestaurant_RestaurantIdx(restaurantId, pageable);
        } else {
            // 검색어가 있으면: 식당 내 필터링 검색
            menus = menuRepository
                    .findByRestaurantAndNameContainingIgnoreCaseOrRestaurantAndDescriptionContainingIgnoreCase(
                            restaurant, keyword,
                            restaurant, keyword,
                            pageable);
        }

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
        Menu menu = menuRepository.findByIdWithRestaurant(id)
                .orElseThrow(() -> new MenuNotFoundException(id));

        return MenuResponse.builder()
                .menuIdx(menu.getMenuIdx())
                .restaurantIdx(menu.getRestaurant().getRestaurantIdx())
                .restaurantName(menu.getRestaurant().getName())
                .name(menu.getName())
                .price(menu.getPrice())
                .description(menu.getDescription())
                .isSignature(menu.getIsSignature())
                .imageUrl(menu.getImageUrl())
                .createdAt(menu.getCreatedAt())
                .build();
    }

    public void deleteMenuById(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new MenuNotFoundException(id));

        //이미지 URL이 존재하면 S3에서 삭제
        String imageUrl = menu.getImageUrl();
        if (imageUrl != null && !imageUrl.isBlank()) {
            try {
                //URL 그대로 넘기면 S3Service가 내부적으로 key를 추출함
                s3Service.deleteFile(imageUrl);
            } catch (Exception e) {
                System.err.println("S3 이미지 삭제 실패: " + e.getMessage());
            }
        }

        //DB에서 메뉴 삭제
        menuRepository.delete(menu);
    }

    //메뉴 정보 수정
    @Transactional
    public MenuResponse updateMenu(Long menuId, AddMenuRequest request) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuNotFoundException(menuId));
//                .orElseThrow(() -> new EntityNotFoundException("메뉴 없음"));

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

    //메뉴 전역 검색
    public Page<MenuSearchResponse> searchMenus(String keyword, Pageable pageable) {
        Page<Menu> menus = menuRepository.findByKeywordWithRestaurant(keyword, pageable);
//        Page<Menu> menus = menuRepository.findByNameContainingOrDescriptionContaining(keyword, keyword, pageable);

        return menus.map(menu -> MenuSearchResponse.builder()
                .menuIdx(menu.getMenuIdx())
                .restaurantIdx(menu.getRestaurant().getRestaurantIdx())
                .restaurantName(menu.getRestaurant().getName())
                .name(menu.getName())
                .price(menu.getPrice())
                .isSignature(menu.getIsSignature())
                .imageUrl(menu.getImageUrl())
                .build());

    }

}




