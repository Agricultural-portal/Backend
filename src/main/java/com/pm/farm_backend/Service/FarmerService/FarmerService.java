package com.pm.farm_backend.Service.FarmerService;

import com.pm.farm_backend.Dto.farmerDto.ProductCreateRequest;
import com.pm.farm_backend.Dto.farmerDto.ProductUpdateRequest;
import com.pm.farm_backend.Dto.farmerDto.FarmerDashboardStatsDTO;
import com.pm.farm_backend.Dto.authDto.UserUpdateRequest;
import com.pm.farm_backend.Model.Product;
import com.pm.farm_backend.Model.Order;
import com.pm.farm_backend.Model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FarmerService {

    // Product Management
    Product createProduct(ProductCreateRequest request, Long farmerId);

    Product createProductWithImage(String name, String description, String price,
            String stock, String unit, String category,
            MultipartFile image, Long farmerId);

    List<Product> getFarmerProducts(Long farmerId);

    Product getFarmerProduct(Long productId, Long farmerId);

    Product updateProduct(Long productId, ProductUpdateRequest request, Long farmerId);

    Product updateProductImage(Long productId, MultipartFile image, Long farmerId);

    void deleteProduct(Long productId, Long farmerId);

    // Order Management
    List<Order> getFarmerOrders(Long farmerId);

    Order getFarmerOrder(Long orderId, Long farmerId);

    void updateOrderStatus(Long orderId, String status, Long farmerId);

    // Dashboard & Analytics
    FarmerDashboardStatsDTO getFarmerDashboardStats(Long farmerId);

    Map<String, Object> getMonthlySales(Long farmerId, Integer year);

    // Profile Management
    User getFarmerProfile(Long farmerId);

    User updateFarmerProfile(Long farmerId, UserUpdateRequest request);

    User updateFarmerProfileImage(Long farmerId, MultipartFile image);
}