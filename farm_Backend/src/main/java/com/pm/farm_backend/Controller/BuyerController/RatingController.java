package com.pm.farm_backend.Controller.BuyerController;

import com.pm.farm_backend.Model.Rating;
import com.pm.farm_backend.Dto.buyerDTO.RatingRequest;
import com.pm.farm_backend.Service.buyerService.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buyer/ratings")
@PreAuthorize("hasRole('BUYER')")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @PostMapping("/add")
    public void addRating(@RequestBody RatingRequest request, Authentication authentication) {
        ratingService.addRating(request, authentication.getName());
    }
    
    @GetMapping("/product/{productId}")
    public List<Rating> getProductRatings(@PathVariable Long productId) {
        return ratingService.getProductRatings(productId);
    }
}
