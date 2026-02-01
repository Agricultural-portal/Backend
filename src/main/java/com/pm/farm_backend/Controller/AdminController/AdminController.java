package com.pm.farm_backend.Controller.AdminController;

import com.pm.farm_backend.Dto.AdminOrderDTO;
import com.pm.farm_backend.Dto.AdminUserDTO;
import com.pm.farm_backend.Dto.SchemeRequest;
import com.pm.farm_backend.Dto.OrderStatusUpdateRequest;
import com.pm.farm_backend.Dto.authDto.UserUpdateRequest;
import com.pm.farm_backend.Model.BankDetails;
import com.pm.farm_backend.Model.FarmerProfile;
import com.pm.farm_backend.Model.GovScheme;
import com.pm.farm_backend.Model.Order;
import com.pm.farm_backend.Model.OrderItem;
import com.pm.farm_backend.Model.Product;
import com.pm.farm_backend.Model.User;
import com.pm.farm_backend.Repositories.BankDetailsRepository;
import com.pm.farm_backend.Repositories.ProductRepository;
import com.pm.farm_backend.Repositories.FarmerProfileRepository;
import com.pm.farm_backend.Repositories.GovSchemeRepository;
import com.pm.farm_backend.Repositories.OrderRepository;
import com.pm.farm_backend.Repositories.UserRepository;
import com.pm.farm_backend.enums.AccountStatus;
import com.pm.farm_backend.enums.OrderStatus;
import com.pm.farm_backend.enums.Role;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private FarmerProfileRepository farmerProfileRepo;

    @Autowired
    private BankDetailsRepository bankDetailsRepo;

    @Autowired
    private GovSchemeRepository govSchemeRepo;

    @Autowired
    private ProductRepository productRepo;

    @PutMapping("/approve/{userId}")
    public ResponseEntity<?> approveUser(@PathVariable Long userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(AccountStatus.ACTIVE);
        userRepo.save(user);

        return ResponseEntity.ok("User approved");
    }

    @GetMapping("/pending")
    public ResponseEntity<java.util.List<User>> getPendingAdmins() {
        return ResponseEntity
                .ok(userRepo.findByRoleAndStatus(com.pm.farm_backend.enums.Role.ADMIN, AccountStatus.PENDING));
    }

    @GetMapping("/farmers")
    public ResponseEntity<java.util.List<AdminUserDTO>> getAllFarmers() {
        java.util.List<User> farmers = userRepo.findByRoleAndIsDeleted(com.pm.farm_backend.enums.Role.FARMER, false);
        java.util.List<AdminUserDTO> farmerDTOs = farmers.stream()
            .map(this::convertToAdminUserDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(farmerDTOs);
    }

    @GetMapping("/buyers")
    public ResponseEntity<java.util.List<AdminUserDTO>> getAllBuyers() {
        java.util.List<User> buyers = userRepo.findByRoleAndIsDeleted(com.pm.farm_backend.enums.Role.BUYER, false);
        java.util.List<AdminUserDTO> buyerDTOs = buyers.stream()
            .map(this::convertToAdminUserDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(buyerDTOs);
    }

    @GetMapping("/count/total-farmers")
    public ResponseEntity<Long> getTotalFarmersCount() {
        return ResponseEntity.ok(userRepo.countByRole(Role.FARMER));
    }

    @GetMapping("/count/active-farmers")
    public ResponseEntity<Long> getActiveFarmersCount() {
        return ResponseEntity.ok(userRepo.countByRoleAndStatus(Role.FARMER, AccountStatus.ACTIVE));
    }

    @GetMapping("/count/inactive-farmers")
    public ResponseEntity<Long> getInactiveFarmersCount() {
        return ResponseEntity.ok(userRepo.countByRoleAndStatus(Role.FARMER, AccountStatus.SUSPENDED));
    }

    @GetMapping("/count/new-farmers-this-month")
    public ResponseEntity<Long> getNewFarmersThisMonthCount() {
        LocalDateTime startOfMonth = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).withHour(0)
                .withMinute(0).withSecond(0).withNano(0);
        return ResponseEntity.ok(userRepo.countByRoleAndCreatedAtAfter(Role.FARMER, startOfMonth));
    }

    @GetMapping("/count/total-buyers")
    public ResponseEntity<Long> getTotalBuyersCount() {
        return ResponseEntity.ok(userRepo.countByRole(Role.BUYER));
    }


    @GetMapping("/count/total-orders")
    public ResponseEntity<Long> getTotalOrdersCount() {
        return ResponseEntity.ok(orderRepo.count());
    }

    @GetMapping("/revenue")
    public ResponseEntity<BigDecimal> getTotalRevenue() {
        BigDecimal revenue = orderRepo.sumTotalAmount();
        return ResponseEntity.ok(revenue != null ? revenue : BigDecimal.ZERO);
    }

    @GetMapping("/count/active-buyers")
    public ResponseEntity<Long> getActiveBuyersCount() {
        return ResponseEntity.ok(userRepo.countByRoleAndStatus(Role.BUYER, AccountStatus.ACTIVE));
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody @Valid UserUpdateRequest dto) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update basic fields
        if (dto.getFirstName() != null) user.setFirst_name(dto.getFirstName());
        if (dto.getLastName() != null) user.setLast_name(dto.getLastName());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getAddresss() != null) user.setAddresss(dto.getAddresss());
        if (dto.getCity() != null) user.setCity(dto.getCity());
        if (dto.getState() != null) user.setState(dto.getState());
        if (dto.getPincode() != null) user.setPincode(dto.getPincode());

        userRepo.save(user);

        // Update farmer-specific fields if user is a farmer
        if (user.getRole() == Role.FARMER && (dto.getFarmSize() != null || dto.getFarmType() != null)) {
            Optional<FarmerProfile> profileOpt = farmerProfileRepo.findByUser(user);
            if (profileOpt.isPresent()) {
                FarmerProfile profile = profileOpt.get();
                if (dto.getFarmSize() != null) profile.setFarmSize(dto.getFarmSize());
                if (dto.getFarmType() != null) profile.setFarmType(dto.getFarmType());
                farmerProfileRepo.save(profile);
            }
        }

        return ResponseEntity.ok("User updated successfully");
    }

    @DeleteMapping("/users/{userId}")
    @Transactional
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Soft delete: Mark user as deleted instead of actually deleting
        user.setIsDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        user.setStatus(AccountStatus.SUSPENDED); // Also suspend the account
        
        userRepo.save(user);
        return ResponseEntity.ok("User marked as deleted successfully");
    }

    @PutMapping("/users/{userId}/status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Toggle between ACTIVE and SUSPENDED
        if (user.getStatus() == AccountStatus.ACTIVE) {
            user.setStatus(AccountStatus.SUSPENDED);
        } else {
            user.setStatus(AccountStatus.ACTIVE);
        }

        userRepo.save(user);
        return ResponseEntity.ok("User status updated to " + user.getStatus());
    }

    // ==================== ORDER MANAGEMENT ENDPOINTS ====================

    @GetMapping("/orders")
    public ResponseEntity<java.util.List<AdminOrderDTO>> getAllOrders() {
        java.util.List<Order> orders = orderRepo.findAll();
        
        java.util.List<AdminOrderDTO> orderDTOs = orders.stream()
            .map(this::convertToAdminOrderDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(orderDTOs);
    }
    
    private AdminUserDTO convertToAdminUserDTO(User user) {
        String name = (user.getFirst_name() != null ? user.getFirst_name() : "") + 
                      " " + 
                      (user.getLast_name() != null ? user.getLast_name() : "");
        name = name.trim();
        
        String location = "";
        if (user.getCity() != null && user.getState() != null) {
            location = user.getCity() + ", " + user.getState();
        } else if (user.getCity() != null) {
            location = user.getCity();
        } else if (user.getState() != null) {
            location = user.getState();
        }
        
        AdminUserDTO.AdminUserDTOBuilder builder = AdminUserDTO.builder()
            .id(user.getId())
            .name(name)
            .email(user.getEmail())
            .phone(user.getPhone())
            .location(location)
            .status(user.getStatus())
            .role(user.getRole())
            .profileImageUrl(user.getProfileImageUrl())
            .createdAt(user.getCreatedAt())
            .addresss(user.getAddresss())
            .city(user.getCity())
            .state(user.getState())
            .pincode(user.getPincode())
            .money(user.getMoney());
        
        // Add rating info if available
        if (user.getAverageRating() != null) {
            builder.averageRating(user.getAverageRating().doubleValue());
        }
        if (user.getTotalRatings() != null) {
            builder.totalRatings(user.getTotalRatings());
        }
        
        // Add farmer-specific fields if user is a farmer
        if (user.getRole() == Role.FARMER) {
            Optional<FarmerProfile> profileOpt = farmerProfileRepo.findByUser(user);
            if (profileOpt.isPresent()) {
                FarmerProfile profile = profileOpt.get();
                builder.farmSize(profile.getFarmSize())
                       .farmType(profile.getFarmType());
            }
        }
        
        return builder.build();
    }
    
    private AdminOrderDTO convertToAdminOrderDTO(Order order) {
        User buyer = order.getUser();
        
        AdminOrderDTO.BuyerInfo buyerInfo = AdminOrderDTO.BuyerInfo.builder()
            .id(buyer.getId())
            .firstName(buyer.getFirst_name())
            .lastName(buyer.getLast_name())
            .email(buyer.getEmail())
            .phone(buyer.getPhone())
            .build();
        
        java.util.List<AdminOrderDTO.OrderItemInfo> itemInfos = order.getItems().stream()
            .map(item -> {
                User farmer = item.getProduct().getFarmer();
                AdminOrderDTO.FarmerInfo farmerInfo = AdminOrderDTO.FarmerInfo.builder()
                    .id(farmer.getId())
                    .firstName(farmer.getFirst_name())
                    .lastName(farmer.getLast_name())
                    .email(farmer.getEmail())
                    .build();
                
                return AdminOrderDTO.OrderItemInfo.builder()
                    .id(item.getId())
                    .productId(item.getProduct().getId())
                    .productName(item.getProduct().getName())
                    .quantity(item.getQuantity())
                    .price(item.getPriceAtPurchase())
                    .farmer(farmerInfo)
                    .build();
            })
            .collect(Collectors.toList());
        
        return AdminOrderDTO.builder()
            .id(order.getId())
            .status(order.getStatus())
            .totalAmount(order.getTotalAmount())
            .shippingAddress(order.getShippingAddress())
            .paymentStatus(order.getPaymentStatus())
            .createdAt(order.getCreatedAt())
            .updatedAt(order.getUpdatedAt())
            .buyer(buyerInfo)
            .items(itemInfos)
            .build();
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, 
                                               @RequestBody @Valid OrderStatusUpdateRequest request) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(request.getStatus());
        orderRepo.save(order);
        
        return ResponseEntity.ok("Order status updated to " + request.getStatus());
    }

    @GetMapping("/orders/stats")
    public ResponseEntity<Map<String, Long>> getOrderStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("PENDING", orderRepo.countByStatus(OrderStatus.PENDING));
        stats.put("DELIVERED", orderRepo.countByStatus(OrderStatus.DELIVERED));
        stats.put("CANCELLED", orderRepo.countByStatus(OrderStatus.CANCELLED));
        stats.put("TOTAL", orderRepo.count());
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return ResponseEntity.ok(order);
    }

    // ==================== Government Schemes Management ====================

    @GetMapping("/schemes")
    public ResponseEntity<java.util.List<GovScheme>> getAllSchemes(@RequestParam(required = false) Boolean isActive) {
        java.util.List<GovScheme> schemes;
        
        if (isActive != null) {
            schemes = govSchemeRepo.findByIsActive(isActive);
        } else {
            schemes = govSchemeRepo.findAll();
        }
        
        // Update isActive status based on deadline for all schemes
        LocalDateTime now = LocalDateTime.now();
        schemes.forEach(scheme -> {
            boolean shouldBeActive = scheme.getDeadline() != null && scheme.getDeadline().isAfter(now);
            if (scheme.isActive() != shouldBeActive) {
                scheme.setActive(shouldBeActive);
                govSchemeRepo.save(scheme);
            }
        });
        
        return ResponseEntity.ok(schemes);
    }

    @GetMapping("/schemes/stats")
    public ResponseEntity<Map<String, Long>> getSchemeStats() {
        // Update all schemes' isActive status based on deadline
        LocalDateTime now = LocalDateTime.now();
        java.util.List<GovScheme> allSchemes = govSchemeRepo.findAll();
        allSchemes.forEach(scheme -> {
            boolean shouldBeActive = scheme.getDeadline() != null && scheme.getDeadline().isAfter(now);
            if (scheme.isActive() != shouldBeActive) {
                scheme.setActive(shouldBeActive);
                govSchemeRepo.save(scheme);
            }
        });
        
        Map<String, Long> stats = new HashMap<>();
        stats.put("TOTAL", govSchemeRepo.count());
        stats.put("ACTIVE", govSchemeRepo.countByIsActive(true));
        stats.put("INACTIVE", govSchemeRepo.countByIsActive(false));
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/schemes/{schemeId}")
    public ResponseEntity<GovScheme> getSchemeById(@PathVariable Long schemeId) {
        GovScheme scheme = govSchemeRepo.findById(schemeId)
                .orElseThrow(() -> new RuntimeException("Scheme not found"));
        
        // Update isActive based on deadline
        LocalDateTime now = LocalDateTime.now();
        boolean shouldBeActive = scheme.getDeadline() != null && scheme.getDeadline().isAfter(now);
        if (scheme.isActive() != shouldBeActive) {
            scheme.setActive(shouldBeActive);
            govSchemeRepo.save(scheme);
        }
        
        return ResponseEntity.ok(scheme);
    }

    @PostMapping("/schemes")
    public ResponseEntity<GovScheme> createScheme(@Valid @RequestBody SchemeRequest request) {
        GovScheme scheme = new GovScheme();
        scheme.setSchemeName(request.getSchemeName());
        scheme.setDescription(request.getDescription());
        scheme.setBenefits(request.getBenefits());
        scheme.setDeadline(request.getDeadline());
        scheme.setApplicationLink(request.getApplicationLink());
        
        // Set isActive based on deadline
        LocalDateTime now = LocalDateTime.now();
        scheme.setActive(request.getDeadline() != null && request.getDeadline().isAfter(now));
        
        GovScheme savedScheme = govSchemeRepo.save(scheme);
        return ResponseEntity.ok(savedScheme);
    }

    @PutMapping("/schemes/{schemeId}")
    public ResponseEntity<GovScheme> updateScheme(
            @PathVariable Long schemeId,
            @Valid @RequestBody SchemeRequest request) {
        
        GovScheme scheme = govSchemeRepo.findById(schemeId)
                .orElseThrow(() -> new RuntimeException("Scheme not found"));
        
        scheme.setSchemeName(request.getSchemeName());
        scheme.setDescription(request.getDescription());
        scheme.setBenefits(request.getBenefits());
        scheme.setDeadline(request.getDeadline());
        scheme.setApplicationLink(request.getApplicationLink());
        
        // Update isActive based on deadline
        LocalDateTime now = LocalDateTime.now();
        scheme.setActive(request.getDeadline() != null && request.getDeadline().isAfter(now));
        
        GovScheme updatedScheme = govSchemeRepo.save(scheme);
        return ResponseEntity.ok(updatedScheme);
    }

    @DeleteMapping("/schemes/{schemeId}")
    public ResponseEntity<Map<String, String>> deleteScheme(@PathVariable Long schemeId) {
        GovScheme scheme = govSchemeRepo.findById(schemeId)
                .orElseThrow(() -> new RuntimeException("Scheme not found"));
        
        govSchemeRepo.delete(scheme);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Scheme deleted successfully");
        return ResponseEntity.ok(response);
    }
}
