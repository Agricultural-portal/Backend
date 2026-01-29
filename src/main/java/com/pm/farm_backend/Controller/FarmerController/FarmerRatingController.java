package com.pm.farm_backend.Controller.FarmerController;

import com.pm.farm_backend.Dto.farmerDto.FarmerRatingResponseDTO;
import com.pm.farm_backend.Service.buyerService.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/farmers")
@CrossOrigin(origins = "*")
public class FarmerRatingController {

    @Autowired
    private RatingService ratingService;

    @Autowired
    private com.pm.farm_backend.Repositories.UserRepository userRepository;

    @GetMapping("/ratings")
    public ResponseEntity<List<FarmerRatingResponseDTO>> getFarmerRatings(java.security.Principal principal) {
        String email = principal.getName();
        System.out.println("DEBUG: Received request for farmer ratings (via token), email: " + email);

        try {
            com.pm.farm_backend.Model.User farmer = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Farmer not found"));
            Long farmerId = farmer.getId();

            List<FarmerRatingResponseDTO> ratings = ratingService.getFarmerRatings(farmerId);
            System.out.println("DEBUG: Returning " + ratings.size() + " ratings");
            return ResponseEntity.ok(ratings);
        } catch (Exception e) {
            System.err.println("ERROR: Failed to fetch ratings: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/ratings/recent")
    public ResponseEntity<List<FarmerRatingResponseDTO>> getRecentFarmerRatings(java.security.Principal principal) {
        String email = principal.getName();
        System.out.println("DEBUG: Fetching recent ratings (via token), email: " + email);

        try {
            com.pm.farm_backend.Model.User farmer = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Farmer not found"));
            Long farmerId = farmer.getId();

            List<FarmerRatingResponseDTO> recentRatings = ratingService.getRecentFarmerRatings(farmerId);
            System.out.println("DEBUG: Found " + recentRatings.size() + " recent ratings");
            return ResponseEntity.ok(recentRatings);
        } catch (Exception e) {
            System.err.println("ERROR: Failed to fetch recent ratings: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
