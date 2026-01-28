package com.pm.farm_backend.Controller.BuyerController;

import com.pm.farm_backend.Dto.buyerDTO.ProductDTO;
import com.pm.farm_backend.Service.buyerService.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buyer/favorites")
@PreAuthorize("hasRole('BUYER')")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/add/{productId}")
    public void addFavorite(@PathVariable Long productId, Authentication authentication) {
        favoriteService.addFavorite(authentication.getName(), productId);
    }

    @DeleteMapping("/remove/{productId}")
    public void removeFavorite(@PathVariable Long productId, Authentication authentication) {
        favoriteService.removeFavorite(authentication.getName(), productId);
    }

    @GetMapping
    public List<ProductDTO> getFavorites(Authentication authentication) {
        return favoriteService.getFavorites(authentication.getName());
    }
}
