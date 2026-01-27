package com.pm.farm_backend.Service.buyerService;

import com.pm.farm_backend.Model.Order;
import com.pm.farm_backend.Dto.buyerDTO.DashboardStatsDTO;
import com.pm.farm_backend.enums.OrderStatus;
import com.pm.farm_backend.Repositories.CartRepository;
import com.pm.farm_backend.Repositories.FavoriteRepository;
import com.pm.farm_backend.Repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pm.farm_backend.Model.User;
import com.pm.farm_backend.Repositories.UserRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BuyerDashboardService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private UserRepository userRepository;

    public DashboardStatsDTO getStats(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Order> orders = orderRepository.findByUserId(user.getId());
        
        DashboardStatsDTO stats = new DashboardStatsDTO();
        stats.setTotalOrders(orders.size());
        stats.setPendingOrders(orders.stream().filter(o -> o.getStatus() == OrderStatus.PENDING).count());
        stats.setInTransitOrders(orders.stream().filter(o -> o.getStatus() == OrderStatus.IN_TRANSIT).count());
        stats.setDeliveredOrders(orders.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).count());
        
        BigDecimal totalSpent = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalSpent(totalSpent);

        cartRepository.findByUserId(user.getId()).ifPresent(cart -> 
            stats.setCartItemsCount(cart.getItems().size())
        );
        
        stats.setFavoritesCount(favoriteRepository.findByUserId(user.getId()).size());
        
        return stats;
    }
}
