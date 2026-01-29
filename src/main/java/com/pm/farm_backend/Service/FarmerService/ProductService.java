package com.pm.farm_backend.Service.FarmerService;

import com.pm.farm_backend.Model.Product;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ProductService {
        com.pm.farm_backend.Dto.farmerDto.ProductResponseDTO addProduct(
                        com.pm.farm_backend.Dto.farmerDto.ProductRequestDTO dto,
                        String email,
                        MultipartFile image);

        List<Product> getAllProducts(String email);

        com.pm.farm_backend.Dto.farmerDto.ProductResponseDTO updateProduct(Long id,
                        com.pm.farm_backend.Dto.farmerDto.ProductRequestDTO updatedProductDto,
                        String email,
                        MultipartFile image);

        void deleteProduct(Long id, String email);
}
