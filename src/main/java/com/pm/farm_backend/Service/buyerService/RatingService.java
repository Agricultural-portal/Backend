package com.pm.farm_backend.Service.buyerService;

import com.pm.farm_backend.Model.Product;
import com.pm.farm_backend.Model.Rating;
import com.pm.farm_backend.Model.User;
import com.pm.farm_backend.Dto.buyerDTO.RatingRequest;
import com.pm.farm_backend.Repositories.ProductRepository;
import com.pm.farm_backend.Repositories.RatingRepository;
import com.pm.farm_backend.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pm.farm_backend.Dto.farmerDto.FarmerRatingResponseDTO;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class RatingService {

        @Autowired
        private RatingRepository ratingRepository;

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private UserRepository userRepository;

        @org.springframework.transaction.annotation.Transactional
        public void addRating(RatingRequest request, String email) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Product product = productRepository.findById(request.getProductId())
                                .orElseThrow(() -> new RuntimeException("Product not found"));

                Rating rating = new Rating();
                rating.setBuyerId(user.getId());
                rating.setOrderId(request.getOrderId());
                rating.setProductId(request.getProductId());
                rating.setFarmerId(product.getFarmer().getId());
                rating.setStars(request.getStars());
                rating.setComment(request.getComment());

                ratingRepository.save(rating);
                System.out.println("DEBUG: Rating saved for product " + request.getProductId());

                // Update product average rating
                List<Rating> productRatings = ratingRepository.findByProductId(request.getProductId());
                System.out.println("DEBUG: Found " + productRatings.size() + " ratings for product "
                                + request.getProductId());

                double average = productRatings.stream()
                                .mapToInt(Rating::getStars)
                                .average()
                                .orElse(0.0);

                System.out.println("DEBUG: Calculated average: " + average);

                // Round to 1 decimal place
                double roundedAverage = Math.round(average * 10.0) / 10.0;
                System.out.println("DEBUG: Rounded average: " + roundedAverage);

                product.setTotalRatings(productRatings.size());
                productRepository.save(product);
                System.out.println("DEBUG: Product updated with new stats");

                // Update farmer average rating
                Long farmerId = product.getFarmer().getId();
                List<Rating> farmerRatings = ratingRepository.findByFarmerId(farmerId);
                System.out.println("DEBUG: Found " + farmerRatings.size() + " ratings for farmer " + farmerId);

                double farmerAverage = farmerRatings.stream()
                                .mapToInt(Rating::getStars)
                                .average()
                                .orElse(0.0);

                // Round to 1 decimal place
                double roundedFarmerAverage = Math.round(farmerAverage * 10.0) / 10.0;
                System.out.println("DEBUG: Calculated farmer average: " + roundedFarmerAverage);

                User farmer = product.getFarmer();
                farmer.setAverageRating(roundedFarmerAverage);
                farmer.setTotalRatings(farmerRatings.size());
                userRepository.save(farmer);
                System.out.println("DEBUG: Farmer stats updated");
        }

        public List<Rating> getProductRatings(Long productId) {
                return ratingRepository.findByProductId(productId);
        }

        public List<FarmerRatingResponseDTO> getFarmerRatings(Long farmerId) {
                List<Rating> ratings = ratingRepository.findByFarmerId(farmerId);
                return mapToFarmerRatingDTOs(ratings);
        }

        public List<FarmerRatingResponseDTO> getRecentFarmerRatings(Long farmerId) {
                // Fetch all ratings and sort in-memory to avoid DB query issues
                List<Rating> allRatings = ratingRepository.findByFarmerId(farmerId);

                List<Rating> recentRatings = allRatings.stream()
                                .sorted(java.util.Comparator.comparing(Rating::getCreatedAt).reversed())
                                .limit(5)
                                .collect(Collectors.toList());

                return mapToFarmerRatingDTOs(recentRatings);
        }

        private List<FarmerRatingResponseDTO> mapToFarmerRatingDTOs(List<Rating> ratings) {
                return ratings.stream().map(rating -> {
                        FarmerRatingResponseDTO dto = new FarmerRatingResponseDTO();
                        dto.setId(rating.getId());

                        // Fetch Buyer Name
                        String buyerName = userRepository.findById(rating.getBuyerId())
                                        .map(User::getFullName)
                                        .orElse("Unknown Buyer");
                        dto.setBuyerName(buyerName);

                        // Fetch Product Name
                        String productName = productRepository.findById(rating.getProductId())
                                        .map(Product::getName)
                                        .orElse("Unknown Product");
                        dto.setProductName(productName);

                        dto.setStars(rating.getStars());
                        dto.setComment(rating.getComment());
                        dto.setCreatedAt(rating.getCreatedAt());
                        return dto;
                }).collect(Collectors.toList());
        }

        // public List<FarmerRatingResponseDTO> getFarmerRatings(Long farmerId) {
        // // DIAGNOSTIC
        // List<Rating> allRatings = ratingRepository.findAll();
        // System.out.println("DEBUG DIAGNOSTIC: Total ratings in DB: " +
        // allRatings.size());
        // for (Rating r : allRatings) {
        // System.out.println("DEBUG: Rating ID=" + r.getId() + ", FarmerID=" +
        // r.getFarmerId()
        // + " (Requested: " + farmerId + ")");
        // }

        // List<Rating> ratings = ratingRepository.findByFarmerId(farmerId);
        // return mapToFarmerRatingDTOs(ratings);
        // }
}
