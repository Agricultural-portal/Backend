package com.pm.farm_backend.Service.buyerService;

import com.pm.farm_backend.Model.Product;
import com.pm.farm_backend.Dto.buyerDTO.ProductDTO;
import com.pm.farm_backend.enums.ProductCategory;
import com.pm.farm_backend.Repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<ProductDTO> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCase(query).stream().map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getProductsByCategory(ProductCategory category) {
        return productRepository.findByCategory(category).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id).map(this::mapToDTO).orElse(null);
    }

    // Helper to map Entity to DTO
    private ProductDTO mapToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setUnit(product.getUnit());
        dto.setCategory(product.getCategory());
        dto.setImageUrl(product.getImageUrl());
        dto.setAverageRating(product.getAverageRating().doubleValue());
        dto.setTotalRatings(product.getTotalRatings());
        if (product.getFarmer() != null) {
            dto.setFarmerName(product.getFarmer().getFirst_name() + " " + product.getFarmer().getLast_name());
            dto.setFarmerRating(product.getFarmer().getAverageRating().doubleValue());
            dto.setFarmerTotalRatings(product.getFarmer().getTotalRatings());
        }
        return dto;
    }
}
