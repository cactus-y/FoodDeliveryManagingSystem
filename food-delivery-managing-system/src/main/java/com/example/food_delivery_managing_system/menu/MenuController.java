package com.example.food_delivery_managing_system.menu;

import com.example.food_delivery_managing_system.menu.dto.AddMenuRequest;
import com.example.food_delivery_managing_system.menu.dto.MenuResponse;
import com.example.food_delivery_managing_system.menu.dto.MenuSearchResponse;
import com.example.food_delivery_managing_system.menu.dto.MenuSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    //식당 메뉴 추가 페이지에서 메뉴 추가
    @PostMapping("/api/restaurants/{restaurantId}/menus")
    public ResponseEntity<MenuResponse> createMenu(
            @PathVariable Long restaurantId,
            @RequestBody AddMenuRequest request
    ) {
        MenuResponse response = menuService.createMenu(restaurantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //식당 페이지에서 해당 식당의 메뉴 조회
    @GetMapping("/api/restaurants/{restaurantId}/menus")
    public ResponseEntity<Page<MenuSummaryResponse>> getMenusByRestaurant(
            @PathVariable Long restaurantId,
            @PageableDefault(size = 10, sort = "menuIdx", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<MenuSummaryResponse> responses = menuService.getMenusByRestaurant(restaurantId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    // 개별 메뉴 조회
    @GetMapping("/api/menu/{menuId}")
    public ResponseEntity<MenuResponse> getMenuById(
            @PathVariable Long menuId
    ) {
        MenuResponse response = menuService.getMenuById(menuId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //개별 메뉴 삭제
    @DeleteMapping("/api/menu/{menuId}")
    public ResponseEntity<Void> deleteMenuById(
            @PathVariable Long menuId
    ) {
        menuService.deleteMenuById(menuId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //개별 메뉴 수정
    @PutMapping("/api/menus/{menuId}")
    public ResponseEntity<MenuResponse> updateMenu(
            @PathVariable Long menuId,
            @RequestBody AddMenuRequest request
    ) {
        MenuResponse response = menuService.updateMenu(menuId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //메뉴 검색
    @GetMapping("/api/menus/search")
    public ResponseEntity<Page<MenuSearchResponse>> searchMenus(
            @RequestParam String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<MenuSearchResponse> responses = menuService.searchMenus(keyword, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

}
