package com.pm.farm_backend.Service.DashboardService;

import com.pm.farm_backend.Dto.DashboardStatsResponse;
import com.pm.farm_backend.Model.User;
import com.pm.farm_backend.Repositories.OrderRepository;
import com.pm.farm_backend.Repositories.ProductRepository;
import com.pm.farm_backend.Repositories.UserRepository;
import com.pm.farm_backend.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public DashboardStatsResponse getDashboardStats() {
        // Count farmers and buyers
        long totalFarmers = userRepository.countByRole(Role.FARMER);
        long totalBuyers = userRepository.countByRole(Role.BUYER);
        
        // Count products
        long totalProducts = productRepository.count();
        
        // Calculate platform revenue (5% of total order amount)
        BigDecimal totalOrderAmount = orderRepository.sumTotalAmount();
        BigDecimal platformRevenue = totalOrderAmount != null 
            ? totalOrderAmount.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        
        // Get recent 5 buyers
        List<User> recentBuyersList = userRepository.findTop5ByRoleOrderByCreatedAtDesc(Role.BUYER);
        List<DashboardStatsResponse.UserSummary> recentBuyers = convertToUserSummary(recentBuyersList);
        
        // Get recent 5 farmers
        List<User> recentFarmersList = userRepository.findTop5ByRoleOrderByCreatedAtDesc(Role.FARMER);
        List<DashboardStatsResponse.UserSummary> recentFarmers = convertToUserSummary(recentFarmersList);
        
        return new DashboardStatsResponse(
            totalFarmers,
            totalBuyers,
            totalProducts,
            platformRevenue,
            recentBuyers,
            recentFarmers
        );
    }
    
    private List<DashboardStatsResponse.UserSummary> convertToUserSummary(List<User> users) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        return users.stream()
            .map(user -> new DashboardStatsResponse.UserSummary(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getCity(),
                user.getProfileImageUrl(),
                user.getCreatedAt() != null ? user.getCreatedAt().format(formatter) : ""
            ))
            .collect(Collectors.toList());
    }
}
