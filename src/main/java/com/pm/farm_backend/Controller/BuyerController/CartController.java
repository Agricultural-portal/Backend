package com.pm.farm_backend.Controller.BuyerController;

import com.pm.farm_backend.Dto.buyerDTO.AddToCartRequest;
import com.pm.farm_backend.Dto.buyerDTO.CartDTO;
import com.pm.farm_backend.Service.buyerService.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buyer/cart")
@PreAuthorize("hasRole('BUYER')")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public CartDTO addToCart(@RequestBody AddToCartRequest request, Authentication authentication) {
        return cartService.addToCart(request, authentication.getName());
    }

    @GetMapping
    public CartDTO getCart(Authentication authentication) {
        return cartService.getCart(authentication.getName());
    }

    @DeleteMapping
    public void clearCart(Authentication authentication) {
        cartService.clearCart(authentication.getName());
    }
}
