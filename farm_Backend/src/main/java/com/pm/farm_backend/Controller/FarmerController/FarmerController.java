package com.pm.farm_backend.Controller.FarmerController;

import com.pm.farm_backend.Dto.farmerDto.ProductCreateRequest;
import com.pm.farm_backend.Dto.farmerDto.ProductUpdateRequest;
import com.pm.farm_backend.Dto.farmerDto.FarmerDashboardStatsDTO;
import com.pm.farm_backend.Model.Product;
import com.pm.farm_backend.Model.Order;
import com.pm.farm_backend.Model.User;
import com.pm.farm_backend.Service.FarmerService.FarmerService;
import com.pm.farm_backend.Security.model.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/farmer")
@PreAuthorize("hasRole('FARMER')")
public class FarmerController {

    @Autowired
    private FarmerService farmerService;

    // ==================== PRODUCT MANAGEMENT ====================

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(
            @Valid @RequestBody ProductCreateRequest request,
            Authentication authentication) {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long farmerId = userDetails.getId();
        
        Product product = farmerService.createProduct(request, farmerId);
        return ResponseEntity.ok(product);
    }

    @PostMapping("/products/with-image")
    public ResponseEntity<Product> createProductWithImage(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") String price,
            @RequestParam("stock") String stock,
            @RequestParam("unit") String unit,
            @RequestParam("category") String category,
            @RequestParam("image") MultipartFile image,
            Authentication authentication) {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long farmerId = userDetails.getId();
        
        Product product = farmerService.createProductWithImage(
            name, description, price, stock, unit, category, image, farmerId);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getMyProducts(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long farmerId = userDetails.getId();
        
        List<Product> products = farmerService.getFarmerProducts(farmerId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<Product> getProduct(
            @PathVariable Long productId,
            Authentication authentication) {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long farmerId = userDetails.getId();
        
        Product product = farmerService.getFarmerProduct(productId, farmerId);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductUpdateRequest request,
            Authentication authentication) {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long farmerId = userDetails.getId();
        
        Product product = farmerService.updateProduct(productId, request, farmerId);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/products/{productId}/image")
    public ResponseEntity<Product> updateProductImage(
            @PathVariable Long productId,
            @RequestParam("image") MultipartFile image,
            Authentication authentication) {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long farmerId = userDetails.getId();
        
        Product product = farmerService.updateProductImage(productId, image, farmerId);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Map<String, String>> deleteProduct(
            @PathVariable Long productId,
            Authentication authentication) {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long farmerId = userDetails.getId();
        
        farmerService.deleteProduct(productId, farmerId);
        return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
    }

    // ==================== ORDER MANAGEMENT ====================

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getMyOrders(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long farmerId = userDetails.getId();
        
        List<Order> orders = farmerService.getFarmerOrders(farmerId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Order> getOrder(
            @PathVariable Long orderId,
            Authentication authentication) {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long farmerId = userDetails.getId();
        
        Order order = farmerService.getFarmerOrder(orderId, farmerId);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<Map<String, String>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status,
            Authentication authentication) {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long farmerId = userDetails.getId();
        
        farmerService.updateOrderStatus(orderId, status, farmerId);
        return ResponseEntity.ok(Map.of("message", "Order status updated successfully"));
    }

    // ==================== DASHBOARD & ANALYTICS ====================

    @GetMapping("/dashboard/stats")
    public ResponseEntity<FarmerDashboardStatsDTO> getDashboardStats(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long farmerId = userDetails.getId();
        
        FarmerDashboardStatsDTO stats = farmerService.getFarmerDashboardStats(farmerId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/sales/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlySales(
            @RequestParam(required = false) Integer year,
            Authentication authentication) {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long farmerId = userDetails.getId();
        
        Map<String, Object> sales = farmerService.getMonthlySales(farmerId, year);
        return ResponseEntity.ok(sales);
    }

    // ==================== PROFILE MANAGEMENT ====================

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long farmerId = userDetails.getId();
        
        User farmer = farmerService.getFarmerProfile(farmerId);
        return ResponseEntity.ok(farmer);
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(
            @Valid @RequestBody com.pm.farm_backend.Dto.authDto.UserUpdateRequest request,
            Authentication authentication) {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long farmerId = userDetails.getId();
        
        User farmer = farmerService.updateFarmerProfile(farmerId, request);
        return ResponseEntity.ok(farmer);
    }
}