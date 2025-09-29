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

    @PostMapping("/api/restaurants/{restaurantId}/menus")
    public ResponseEntity<MenuResponse> createMenu(
            @PathVariable Long restaurantId,
            @RequestBody AddMenuRequest request
    ) {
        MenuResponse response = menuService.createMenu(restaurantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/restaurants/{restaurantId}/menus")
    public ResponseEntity<List<MenuSummaryResponse>> getMenusByRestaurant(
            @PathVariable Long restaurantId
    ) {
        List<MenuSummaryResponse> responses = menuService.getMenusByRestaurant(restaurantId);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @GetMapping("/api/menu/{menuId}")
    public ResponseEntity<MenuResponse> getMenuById(
            @PathVariable Long menuId
    ) {
        MenuResponse response = menuService.getMenuById(menuId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/api/menu/{menuId}")
    public ResponseEntity<Void> deleteMenuById(
            @PathVariable Long menuId
    ) {
        menuService.deleteMenuById(menuId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/api/menus/{menuId}")
    public ResponseEntity<MenuResponse> updateMenu(
            @PathVariable Long menuId,
            @RequestBody AddMenuRequest request
    ) {
        MenuResponse response = menuService.updateMenu(menuId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/api/menus/search")
    public ResponseEntity<Page<MenuSearchResponse>> searchMenus(
            @RequestParam String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<MenuSearchResponse> responses = menuService.searchMenus(keyword, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

}
