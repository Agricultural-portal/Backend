package com.pm.farm_backend.Service.FarmerService;

import com.pm.farm_backend.Dto.farmerDto.ProductCreateRequest;
import com.pm.farm_backend.Dto.farmerDto.ProductUpdateRequest;
import com.pm.farm_backend.Dto.farmerDto.FarmerDashboardStatsDTO;
import com.pm.farm_backend.Dto.authDto.UserUpdateRequest;
import com.pm.farm_backend.Dto.CreateNotificationDto;
import com.pm.farm_backend.Exception.ResourceNotFoundException;
import com.pm.farm_backend.Exception.BadRequestException;
import com.pm.farm_backend.Exception.UnauthorizedException;
import com.pm.farm_backend.Model.*;
import com.pm.farm_backend.Repositories.*;
import com.pm.farm_backend.Service.ImageService;
import com.pm.farm_backend.Service.NotificationService;
import com.pm.farm_backend.enums.OrderStatus;
import com.pm.farm_backend.enums.ProductCategory;
import com.pm.farm_backend.enums.ProductUnit;
import com.pm.farm_backend.enums.Role;
import com.pm.farm_backend.enums.NotificationType;
import com.pm.farm_backend.enums.NotificationPriority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class FarmerServiceImpl implements FarmerService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private FarmerProfileRepository farmerProfileRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private NotificationService notificationService;

    // ==================== PRODUCT MANAGEMENT ====================

    @Override
    public Product createProduct(ProductCreateRequest request, Long farmerId) {
        User farmer = userRepository.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found"));

        if (farmer.getRole() != Role.FARMER) {
            throw new UnauthorizedException("User is not a farmer");
        }

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setUnit(request.getUnit());
        product.setCategory(request.getCategory());
        product.setImageUrl(request.getImageUrl());
        product.setStatus(com.pm.farm_backend.enums.ProductStatus.AVAILABLE);
        product.setFarmer(farmer);

        return productRepository.save(product);
    }

    @Override
    public Product createProductWithImage(String name, String description, String price,
            String stock, String unit, String category,
            MultipartFile image, Long farmerId) {
        User farmer = userRepository.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found"));

        if (farmer.getRole() != Role.FARMER) {
            throw new UnauthorizedException("User is not a farmer");
        }

        // Upload image
        String imageUrl;
        try {
            imageUrl = imageService.uploadImage(image);
        } catch (IOException e) {
            throw new BadRequestException("Failed to upload image: " + e.getMessage());
        }

        Product product = new Product();
        product.setName(name);
        product.setDescription(description);

        try {
            product.setPrice(new BigDecimal(price));
            product.setStock(Integer.parseInt(stock));
            product.setUnit(ProductUnit.valueOf(unit.toUpperCase()));
            if (category != null && !category.isEmpty()) {
                product.setCategory(ProductCategory.valueOf(category.toUpperCase()));
            }
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid number format: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid product data: " + e.getMessage());
        }

        product.setImageUrl(imageUrl);
        product.setStatus(com.pm.farm_backend.enums.ProductStatus.AVAILABLE);
        product.setFarmer(farmer);

        return productRepository.save(product);
    }

    @Override
    public List<Product> getFarmerProducts(Long farmerId) {
        User farmer = userRepository.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found"));

        return productRepository.findByFarmer(farmer);
    }

    @Override
    public Product getFarmerProduct(Long productId, Long farmerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getFarmer().getId().equals(farmerId)) {
            throw new UnauthorizedException("Product does not belong to this farmer");
        }

        return product;
    }

    @Override
    public Product updateProduct(Long productId, ProductUpdateRequest request, Long farmerId) {
        Product product = getFarmerProduct(productId, farmerId);

        if (request.getName() != null)
            product.setName(request.getName());
        if (request.getDescription() != null)
            product.setDescription(request.getDescription());
        if (request.getPrice() != null)
            product.setPrice(request.getPrice());
        if (request.getStock() != null)
            product.setStock(request.getStock());
        if (request.getUnit() != null)
            product.setUnit(request.getUnit());
        if (request.getCategory() != null)
            product.setCategory(request.getCategory());
        if (request.getImageUrl() != null)
            product.setImageUrl(request.getImageUrl());
        if (request.getStatus() != null)
            product.setStatus(request.getStatus());

        return productRepository.save(product);
    }

    @Override
    public Product updateProductImage(Long productId, MultipartFile image, Long farmerId) {
        Product product = getFarmerProduct(productId, farmerId);

        String imageUrl;
        try {
            imageUrl = imageService.uploadImage(image);
        } catch (IOException e) {
            throw new BadRequestException("Failed to upload image: " + e.getMessage());
        }

        product.setImageUrl(imageUrl);

        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long productId, Long farmerId) {
        Product product = getFarmerProduct(productId, farmerId);
        productRepository.delete(product);
    }

    // ==================== ORDER MANAGEMENT ====================

    @Override
    public List<Order> getFarmerOrders(Long farmerId) {
        return orderRepository.findByFarmerId(farmerId);
    }

    @Override
    public Order getFarmerOrder(Long orderId, Long farmerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Check if any order item belongs to this farmer
        boolean belongsToFarmer = order.getItems().stream()
                .anyMatch(item -> item.getProduct().getFarmer().getId().equals(farmerId));

        if (!belongsToFarmer) {
            throw new UnauthorizedException("Order does not contain products from this farmer");
        }

        return order;
    }

    @Override
    public void updateOrderStatus(Long orderId, String status, Long farmerId) {
        log.info("========================================");
        log.info("UPDATE ORDER STATUS CALLED");
        log.info("Order ID: {}, New Status: {}, Farmer ID: {}", orderId, status, farmerId);
        log.info("========================================");
        
        Order order = getFarmerOrder(orderId, farmerId);
        log.info("Current order status: {}", order.getStatus());

        try {
            OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
            OrderStatus oldStatus = order.getStatus();
            
            log.info("Old Status: {}, New Status: {}", oldStatus, newStatus);
            
            // If order is being marked as DELIVERED, transfer money from buyer to farmers
            if (newStatus == OrderStatus.DELIVERED && oldStatus != OrderStatus.DELIVERED) {
                log.info("Conditions met for money transfer! Calling transferMoneyOnDelivery...");
                transferMoneyOnDelivery(order);
            } else {
                log.warn("Money transfer NOT triggered. NewStatus={}, OldStatus={}", newStatus, oldStatus);
            }
            
            order.setStatus(newStatus);
            orderRepository.save(order);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid order status: " + status);
        }
    }

    private void transferMoneyOnDelivery(Order order) {
        log.info("=== Starting Money Transfer for Order #{} ===", order.getId());
        User buyer = order.getUser();
        BigDecimal totalAmount = order.getTotalAmount();
        log.info("Order Total: ₹{}", totalAmount);

        // Check if buyer has sufficient balance
        BigDecimal buyerBalance = buyer.getMoney() != null ? buyer.getMoney() : BigDecimal.ZERO;
        log.info("Buyer {} (ID: {}) Balance Before: ₹{}", buyer.getEmail(), buyer.getId(), buyerBalance);
        
        if (buyerBalance.compareTo(totalAmount) < 0) {
            log.error("Insufficient balance! Required: ₹{}, Available: ₹{}", totalAmount, buyerBalance);
            throw new BadRequestException("Buyer has insufficient wallet balance. Required: ₹" + totalAmount + ", Available: ₹" + buyerBalance);
        }

        // Deduct money from buyer
        BigDecimal newBuyerBalance = buyerBalance.subtract(totalAmount);
        buyer.setMoney(newBuyerBalance);
        userRepository.save(buyer);
        log.info("✓ Deducted ₹{} from buyer. New Balance: ₹{}", totalAmount, newBuyerBalance);
        
        // Send notification to buyer
        try {
            CreateNotificationDto buyerNotification = new CreateNotificationDto();
            buyerNotification.setUserId(buyer.getId());
            buyerNotification.setType(NotificationType.ORDER);
            buyerNotification.setTitle("Payment Successful");
            buyerNotification.setMessage("₹" + totalAmount + " has been deducted from your wallet for Order #" + order.getId());
            buyerNotification.setPriority(NotificationPriority.HIGH);
            notificationService.createNotification(buyerNotification);
            log.info("Notification sent to buyer: {}", buyer.getEmail());
        } catch (Exception e) {
            log.error("Failed to send notification to buyer: {}", e.getMessage());
        }

        // Transfer money to farmers based on items in the order
        Map<User, BigDecimal> farmerAmounts = new HashMap<>();
        
        log.info("Processing {} order items...", order.getItems().size());
        for (OrderItem item : order.getItems()) {
            User farmer = item.getProduct().getFarmer();
            BigDecimal itemTotal = item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity()));
            log.info("  Item: {} x {} @ ₹{} = ₹{} (Farmer: {})", 
                item.getProduct().getName(), 
                item.getQuantity(), 
                item.getPriceAtPurchase(), 
                itemTotal,
                farmer.getEmail());
            
            farmerAmounts.merge(farmer, itemTotal, BigDecimal::add);
        }

        // Add money to each farmer's wallet
        log.info("Crediting {} farmer(s)...", farmerAmounts.size());
        for (Map.Entry<User, BigDecimal> entry : farmerAmounts.entrySet()) {
            User farmer = entry.getKey();
            BigDecimal amount = entry.getValue();
            
            BigDecimal farmerBalanceBefore = farmer.getMoney() != null ? farmer.getMoney() : BigDecimal.ZERO;
            BigDecimal farmerBalanceAfter = farmerBalanceBefore.add(amount);
            farmer.setMoney(farmerBalanceAfter);
            userRepository.save(farmer);
            
            log.info("✓ Credited ₹{} to Farmer {} (ID: {}). Balance: ₹{} → ₹{}", 
                amount, farmer.getEmail(), farmer.getId(), farmerBalanceBefore, farmerBalanceAfter);
            
            // Send notification to farmer
            try {
                CreateNotificationDto farmerNotification = new CreateNotificationDto();
                farmerNotification.setUserId(farmer.getId());
                farmerNotification.setType(NotificationType.ORDER);
                farmerNotification.setTitle("Payment Received");
                farmerNotification.setMessage("You received ₹" + amount + " for Order #" + order.getId());
                farmerNotification.setPriority(NotificationPriority.HIGH);
                notificationService.createNotification(farmerNotification);
                log.info("Notification sent to farmer: {}", farmer.getEmail());
            } catch (Exception e) {
                log.error("Failed to send notification to farmer: {}", e.getMessage());
            }
        }
        
        log.info("=== Money Transfer Completed Successfully ===");
    }

    // ==================== DASHBOARD & ANALYTICS ====================

    @Override
    public FarmerDashboardStatsDTO getFarmerDashboardStats(Long farmerId) {
        User farmer = userRepository.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found"));

        FarmerDashboardStatsDTO stats = new FarmerDashboardStatsDTO();

        // Product stats
        List<Product> products = productRepository.findByFarmer(farmer);
        stats.setTotalProducts((long) products.size());
        stats.setActiveProducts(products.stream()
                .filter(p -> p.getStock() > 0)
                .count());
        stats.setOutOfStockProducts(products.stream()
                .filter(p -> p.getStock() == 0)
                .count());

        // Order stats
        List<Order> orders = orderRepository.findByFarmerId(farmerId);
        stats.setTotalOrders((long) orders.size());
        stats.setPendingOrders(orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PENDING)
                .count());
        stats.setCompletedOrders(orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .count());
        stats.setCancelledOrders(orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.CANCELLED)
                .count());

        // Revenue stats
        BigDecimal totalRevenue = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalRevenue(totalRevenue);

        // Monthly revenue
        LocalDateTime startOfMonth = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth());
        BigDecimal monthlyRevenue = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .filter(o -> o.getCreatedAt().isAfter(startOfMonth))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setMonthlyRevenue(monthlyRevenue);

        // Weekly revenue
        LocalDateTime startOfWeek = LocalDateTime.now().minusDays(7);
        BigDecimal weeklyRevenue = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .filter(o -> o.getCreatedAt().isAfter(startOfWeek))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setWeeklyRevenue(weeklyRevenue);

        // Rating stats
        List<Rating> ratings = ratingRepository.findByFarmerId(farmerId);
        if (!ratings.isEmpty()) {
            double avgRating = ratings.stream()
                    .mapToInt(Rating::getRating)
                    .average()
                    .orElse(0.0);
            stats.setAverageRating(avgRating);
            stats.setTotalReviews((long) ratings.size());
        } else {
            stats.setAverageRating(0.0);
            stats.setTotalReviews(0L);
        }

        // Recent activity
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        stats.setNewOrdersToday(orders.stream()
                .filter(o -> o.getCreatedAt().isAfter(today))
                .count());
        stats.setNewOrdersThisWeek(orders.stream()
                .filter(o -> o.getCreatedAt().isAfter(startOfWeek))
                .count());

        // Top selling product (simplified)
        if (!products.isEmpty()) {
            Product topProduct = products.get(0); // Simplified - should be based on actual sales
            stats.setTopSellingProductName(topProduct.getName());
            stats.setTopSellingProductSales(0L); // Would need order items analysis
        }

        return stats;
    }

    @Override
    public Map<String, Object> getMonthlySales(Long farmerId, Integer year) {
        final Integer finalYear = (year == null) ? LocalDateTime.now().getYear() : year;

        List<Order> orders = orderRepository.findByFarmerId(farmerId);

        // Filter orders by year and delivered status
        List<Order> yearOrders = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .filter(o -> o.getCreatedAt().getYear() == finalYear)
                .collect(Collectors.toList());

        // Group by month
        Map<Integer, BigDecimal> monthlySales = new HashMap<>();
        for (int month = 1; month <= 12; month++) {
            monthlySales.put(month, BigDecimal.ZERO);
        }

        for (Order order : yearOrders) {
            int month = order.getCreatedAt().getMonthValue();
            monthlySales.put(month, monthlySales.get(month).add(order.getTotalAmount()));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("year", finalYear);
        result.put("monthlySales", monthlySales);
        result.put("totalYearSales", monthlySales.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return result;
    }

    // ==================== PROFILE MANAGEMENT ====================

    @Override
    public User getFarmerProfile(Long farmerId) {
        User farmer = userRepository.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found"));

        return farmer;
    }

    @Override
    public User updateFarmerProfile(Long farmerId, UserUpdateRequest request) {
        User farmer = userRepository.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found"));

        if (request.getFirstName() != null)
            farmer.setFirst_name(request.getFirstName());
        if (request.getLastName() != null)
            farmer.setLast_name(request.getLastName());
        if (request.getEmail() != null)
            farmer.setEmail(request.getEmail());
        if (request.getPhone() != null)
            farmer.setPhone(request.getPhone());
        if (request.getAddresss() != null)
            farmer.setAddresss(request.getAddresss());
        if (request.getCity() != null)
            farmer.setCity(request.getCity());
        if (request.getState() != null)
            farmer.setState(request.getState());
        if (request.getPincode() != null)
            farmer.setPincode(request.getPincode());

        // Update farmer profile if needed
        if (request.getFarmSize() != null || request.getFarmType() != null) {
            Optional<FarmerProfile> profileOpt = farmerProfileRepository.findByUser(farmer);
            if (profileOpt.isPresent()) {
                FarmerProfile profile = profileOpt.get();
                if (request.getFarmSize() != null)
                    profile.setFarmSize(request.getFarmSize());
                if (request.getFarmType() != null)
                    profile.setFarmType(request.getFarmType());
                farmerProfileRepository.save(profile);
            }
        }

        return userRepository.save(farmer);
    }

    @Override
    public User updateFarmerProfileImage(Long farmerId, MultipartFile image) {
        User farmer = userRepository.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found"));

        String imageUrl;
        try {
            imageUrl = imageService.uploadImage(image);
        } catch (IOException e) {
            throw new BadRequestException("Failed to upload image: " + e.getMessage());
        }

        farmer.setProfileImageUrl(imageUrl);
        return userRepository.save(farmer);
    }
}