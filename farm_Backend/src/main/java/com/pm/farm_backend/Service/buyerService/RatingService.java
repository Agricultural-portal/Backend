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

import java.util.List;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;

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
    }
    
    public List<Rating> getProductRatings(Long productId) {
        return ratingRepository.findByProductId(productId);
    }
}
