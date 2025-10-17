package com.example.food_delivery_managing_system.menu;

import com.example.food_delivery_managing_system.menu.dto.MenuResponse;
import com.example.food_delivery_managing_system.menu.dto.MenuSearchResponse;
import com.example.food_delivery_managing_system.menu.dto.MenuSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MenuPageController {
    private final MenuService menuService;

    //식당의 메뉴 추가/수정 페이지로 매핑
    @GetMapping({"/restaurant/{restaurantId}/menus/new", "/restaurant/{restaurantId}/menus/{menuId}/edit"})
    public String menuTestPage(@PathVariable Long restaurantId,
                               @PathVariable(required = false) Long menuId,
                               Model model) {

        model.addAttribute("restaurantId", restaurantId);

        if (menuId != null) {
            MenuResponse menuResponse = menuService.getMenuById(menuId);
            model.addAttribute("menuResponse", menuResponse);
            model.addAttribute("menuId", menuId);
            model.addAttribute("mode", "edit");
        } else {
            model.addAttribute("mode", "create");
        }

        return "menu/menu-form";
    }

    //식당의 메뉴 목록으로
    @GetMapping("/restaurants/{restaurantId}/menus")
    public String getMenusByRestaurant(
            @PathVariable Long restaurantId,
            @RequestParam(name = "restaurantKeyword",required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable,
            Model model){

        Page<MenuSummaryResponse> page = menuService.getMenusByRestaurant(restaurantId, keyword, pageable);

        int blockSize = 5; // 페이지가 많을 시 번호를 5개씩 보여줌
        int current = page.getNumber();
        int start = (current / blockSize) * blockSize;
        int end = Math.min(start + blockSize, page.getTotalPages() - 1);

        model.addAttribute("start", start);
        model.addAttribute("end", end);

        model.addAttribute("page", page);
        model.addAttribute("restaurantKeyword", keyword);
        model.addAttribute("restaurantId", restaurantId);
        String restaurantName = menuService.getRestaurantNameById(restaurantId);
        model.addAttribute("restaurantName", restaurantName);

        return "menu/restaurant-menu-list";
    }

    //각 메뉴 상세 페이지로
    @GetMapping("/menus/{menuId}")
    public String getMenusByMenuId(
            @PathVariable Long menuId,
            Model model
    ){
        MenuResponse menuResponse = menuService.getMenuById(menuId);
        model.addAttribute("menuId", menuId);
        model.addAttribute("menuResponse", menuResponse);

        return "menu/menu-detail";
    }


    //메뉴 전역 검색 페이지로
    @GetMapping("/menus/search")
    public String searchMenus(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10) Pageable pageable,
            Model model
    ){
        Page<MenuSearchResponse> page = menuService.searchMenus(keyword, pageable);

        int blockSize = 5; // 페이지가 많을 시 번호를 5개씩 보여줌
        int current = page.getNumber();
        int start = (current / blockSize) * blockSize;
        int end = Math.min(start + blockSize, page.getTotalPages() - 1);

        model.addAttribute("start", start);
        model.addAttribute("end", end);

        model.addAttribute("page", page);
        model.addAttribute("keyword", keyword);
        return "menu/menu-search-page";
    }

}