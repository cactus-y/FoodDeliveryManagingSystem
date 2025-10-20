package com.example.food_delivery_managing_system.menu;

import com.example.food_delivery_managing_system.menu.dto.AddMenuRequest;
import com.example.food_delivery_managing_system.menu.dto.MenuResponse;
import com.example.food_delivery_managing_system.menu.dto.MenuSearchResponse;
import com.example.food_delivery_managing_system.menu.dto.MenuSummaryResponse;
import com.example.food_delivery_managing_system.user.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    //식당 메뉴 추가 페이지에서 메뉴 추가
    @PostMapping("/api/restaurants/{restaurantId}/menus")
    public ResponseEntity<?> createMenu(
            @PathVariable Long restaurantId,
            @RequestBody AddMenuRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        // 로그인 안 한 사용자 → 401
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        // 현재 로그인한 유저가 이 메뉴의 식당 OWNER인지 확인
        boolean isOwner = menuService.isRestaurantOwner(
                user.getUser().getUserId(),
                restaurantId
        );

        // 본인 소유 식당이 아닐 경우 → 403 Forbidden
        if (!isOwner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("본 식당 수정 권한이 없습니다.");
        }
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
    public ResponseEntity<?> deleteMenuById(
            @PathVariable Long menuId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        // 로그인 안 한 사용자 → 401
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        // 메뉴 정보 조회
        MenuResponse menu = menuService.getMenuById(menuId);

        // 현재 로그인한 유저가 이 메뉴의 식당 OWNER인지 확인
        boolean isOwner = menuService.isRestaurantOwner(
                user.getUser().getUserId(),
                menu.getRestaurantIdx()
        );

        // 본인 소유 식당이 아닐 경우 → 403 Forbidden
        if (!isOwner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("본 메뉴의 삭제 권한이 없습니다.");
        }

        //  권한 검증 통과 시 실제 삭제 수행
        menuService.deleteMenuById(menuId);
        return ResponseEntity.noContent().build();
    }

    //개별 메뉴 수정
    @PutMapping("/api/menus/{menuId}")
    public ResponseEntity<?> updateMenu(
            @PathVariable Long menuId,
            @RequestBody AddMenuRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        // 로그인 안 한 사용자 → 401
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        // 메뉴 정보 조회
        MenuResponse menu = menuService.getMenuById(menuId);

        // 현재 로그인한 유저가 이 메뉴의 식당 OWNER인지 확인
        boolean isOwner = menuService.isRestaurantOwner(
                user.getUser().getUserId(),
                menu.getRestaurantIdx()
        );

        // 본인 소유 식당이 아닐 경우 → 403 Forbidden
        if (!isOwner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("본 메뉴의 수정 권한이 없습니다.");
        }

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
