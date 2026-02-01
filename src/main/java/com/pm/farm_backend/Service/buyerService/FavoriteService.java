package com.pm.farm_backend.Service.buyerService;

import com.pm.farm_backend.Model.Favorite;
import com.pm.farm_backend.Model.Product;
import com.pm.farm_backend.Model.User;
import com.pm.farm_backend.Dto.buyerDTO.ProductDTO;
import com.pm.farm_backend.Repositories.FavoriteRepository;
import com.pm.farm_backend.Repositories.ProductRepository;
import com.pm.farm_backend.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    public void addFavorite(String email, Long productId) {
        User user = userRepository.findByEmail(email).orElseThrow();
        if (!favoriteRepository.existsByUserIdAndProductId(user.getId(), productId)) {
            Product product = productRepository.findById(productId).orElseThrow();
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setProduct(product);
            favoriteRepository.save(favorite);
        }
    }

    public void removeFavorite(String email, Long productId) {
        User user = userRepository.findByEmail(email).orElseThrow();
        favoriteRepository.findByUserIdAndProductId(user.getId(), productId)
                .ifPresent(favoriteRepository::delete);
    }

    public List<ProductDTO> getFavorites(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return favoriteRepository.findByUserId(user.getId()).stream()
                .map(fav -> {
                    Product p = fav.getProduct();
                    ProductDTO dto = new ProductDTO();
                    dto.setId(p.getId());
                    dto.setName(p.getName());
                    dto.setDescription(p.getDescription());
                    dto.setPrice(p.getPrice());
                    dto.setStock(p.getStock());
                    dto.setUnit(p.getUnit());
                    dto.setCategory(p.getCategory());
                    dto.setImageUrl(p.getImageUrl());
                    if (p.getFarmer() != null) {
                        dto.setFarmerName(p.getFarmer().getFirst_name() + " " + p.getFarmer().getLast_name());
                    }
                    return dto;
                }).collect(Collectors.toList());
    }
}
