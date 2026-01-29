package com.pm.farm_backend.Service.FarmerService;

import com.pm.farm_backend.Model.Order;
import java.util.List;

public interface OrderService {
    List<com.pm.farm_backend.Dto.farmerDto.FarmerOrderItemDTO> getOrdersForProduct(Long productId);

    List<com.pm.farm_backend.Dto.farmerDto.FarmerOrderItemDTO> getAllOrdersForFarmer(String email);

    Order getOrderById(Long id);

    void updateOrderStatus(Long orderId, String status);

    Order placeOrder(Order order);

    String forceCreateIncome(Long orderId);

    void updatePaymentStatus(Long orderId, String paymentStatus);
}
