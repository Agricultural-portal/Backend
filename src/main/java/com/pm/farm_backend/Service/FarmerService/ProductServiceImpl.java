package com.pm.farm_backend.Service.FarmerService;

import com.pm.farm_backend.Model.Product;
import com.pm.farm_backend.Model.User;
import com.pm.farm_backend.Repositories.ProductRepository;
import com.pm.farm_backend.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public com.pm.farm_backend.Dto.farmerDto.ProductResponseDTO addProduct(
            com.pm.farm_backend.Dto.farmerDto.ProductRequestDTO dto,
            String email,
            MultipartFile image) {

        User farmer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        if (farmer.getRole() != com.pm.farm_backend.enums.Role.FARMER) {
            throw new RuntimeException("User is not a farmer");
        }

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = cloudinaryService.uploadImage(image);
        }

        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setUnit(dto.getUnit());
        product.setCategory(dto.getCategory());
        product.setImageUrl(imageUrl);
        product.setFarmer(farmer);
        product.setStatus(dto.getStock() > 0 ? com.pm.farm_backend.enums.ProductStatus.AVAILABLE
                : com.pm.farm_backend.enums.ProductStatus.OUT_OF_STOCK);

        Product saved = productRepository.save(product);

        com.pm.farm_backend.Dto.farmerDto.ProductResponseDTO response = new com.pm.farm_backend.Dto.farmerDto.ProductResponseDTO();
        response.setId(saved.getId());
        response.setName(saved.getName());
        response.setDescription(saved.getDescription());
        response.setPrice(saved.getPrice());
        response.setStock(saved.getStock());
        response.setUnit(saved.getUnit().name());
        response.setCategory(saved.getCategory().name());
        response.setImageUrl(saved.getImageUrl());
        response.setFarmerId(saved.getFarmer().getId());
        response.setStatus(saved.getStatus().name());

        return response;
    }

    @Override
    public List<Product> getAllProducts(String email) {
        User farmer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));
        return productRepository.findByFarmer_IdAndStatus(farmer.getId(),
                com.pm.farm_backend.enums.ProductStatus.AVAILABLE);
    }

    @Override
    public com.pm.farm_backend.Dto.farmerDto.ProductResponseDTO updateProduct(Long id,
            com.pm.farm_backend.Dto.farmerDto.ProductRequestDTO updatedProductDto,
            String email,
            MultipartFile image) {

        User farmer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        return productRepository.findById(id).map(existing -> {
            if (!existing.getFarmer().getId().equals(farmer.getId())) {
                throw new RuntimeException("You are not authorized to update this product");
            }

            existing.setName(updatedProductDto.getName());
            existing.setDescription(updatedProductDto.getDescription());
            existing.setPrice(updatedProductDto.getPrice());
            existing.setStock(updatedProductDto.getStock());
            existing.setUnit(updatedProductDto.getUnit());
            existing.setCategory(updatedProductDto.getCategory());

            if (image != null && !image.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(image);
                existing.setImageUrl(imageUrl);
            }

            // Auto-update status based on stock if not explicitly DELETED
            if (existing.getStatus() != com.pm.farm_backend.enums.ProductStatus.DELETED) {
                existing.setStatus(existing.getStock() > 0 ? com.pm.farm_backend.enums.ProductStatus.AVAILABLE
                        : com.pm.farm_backend.enums.ProductStatus.OUT_OF_STOCK);
            }

            Product saved = productRepository.save(existing);

            com.pm.farm_backend.Dto.farmerDto.ProductResponseDTO response = new com.pm.farm_backend.Dto.farmerDto.ProductResponseDTO();
            response.setId(saved.getId());
            response.setName(saved.getName());
            response.setDescription(saved.getDescription());
            response.setPrice(saved.getPrice());
            response.setStock(saved.getStock());
            response.setUnit(saved.getUnit().name());
            response.setCategory(saved.getCategory().name());
            response.setImageUrl(saved.getImageUrl());
            response.setFarmerId(saved.getFarmer().getId());
            response.setStatus(saved.getStatus().name());

            return response;
        }).orElseThrow(() -> new RuntimeException("Product not found with id " + id));
    }

    @Override
    public void deleteProduct(Long id, String email) {
        User farmer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + id));

        if (!product.getFarmer().getId().equals(farmer.getId())) {
            throw new RuntimeException("You are not authorized to delete this product");
        }

        product.setStatus(com.pm.farm_backend.enums.ProductStatus.DELETED);
        productRepository.save(product);
    }
}
