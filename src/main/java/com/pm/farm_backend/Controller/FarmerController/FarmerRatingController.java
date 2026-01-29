package com.pm.farm_backend.Controller.FarmerController;

import com.pm.farm_backend.Dto.farmerDto.FarmerRatingResponseDTO;
import com.pm.farm_backend.Service.buyerService.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/farmers")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
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
    public ResponseEntity<?> getRecentFarmerRatings(java.security.Principal principal) {
        if (principal == null) {
            System.err.println("ERROR: Principal is null - user not authenticated");
            return ResponseEntity.status(401).body("User not authenticated");
        }

        String email = principal.getName();
        System.out.println("DEBUG: Fetching recent ratings (via token), email: " + email);

        try {
            com.pm.farm_backend.Model.User farmer = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Farmer not found with email: " + email));
            Long farmerId = farmer.getId();
            System.out.println("DEBUG: Found farmer with ID: " + farmerId);

            List<FarmerRatingResponseDTO> recentRatings = ratingService.getRecentFarmerRatings(farmerId);
            System.out.println("DEBUG: Found " + recentRatings.size() + " recent ratings");

            // Log first rating if exists
            if (!recentRatings.isEmpty()) {
                FarmerRatingResponseDTO first = recentRatings.get(0);
                System.out.println("DEBUG: First rating - Buyer: " + first.getBuyerName() +
                        ", Stars: " + first.getStars() + ", Comment: " + first.getComment());
            }

            return ResponseEntity.ok(recentRatings);
        } catch (Exception e) {
            System.err.println("ERROR: Failed to fetch recent ratings: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error fetching ratings: " + e.getMessage());
        }
    }
}
