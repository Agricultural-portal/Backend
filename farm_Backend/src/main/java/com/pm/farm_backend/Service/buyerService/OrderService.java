package com.pm.farm_backend.Service.buyerService;

import com.pm.farm_backend.Model.*;
import com.pm.farm_backend.Dto.buyerDTO.OrderDTO;
import com.pm.farm_backend.Dto.buyerDTO.PlaceOrderRequest;
import com.pm.farm_backend.Dto.buyerDTO.OrderHistoryDTO;
import com.pm.farm_backend.enums.OrderStatus;
import com.pm.farm_backend.Repositories.CartRepository;
import com.pm.farm_backend.Repositories.OrderRepository;
import com.pm.farm_backend.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public OrderDTO placeOrder(PlaceOrderRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Validate and reduce stock
        for (com.pm.farm_backend.Model.CartItem item : cart.getItems()) {
            item.getProduct().reduceStock(item.getQuantity());
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus("PENDING"); // Fixed non-nullable field
        order.setShippingAddress(request.getShippingAddress());
        
        // Use totalAmount from request if provided (includes taxes/fees), otherwise use cart total
        BigDecimal orderTotal = request.getTotalAmount() != null ? 
            request.getTotalAmount() : cart.getTotalAmount();
        order.setTotalAmount(orderTotal);

        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartItem.getProduct().getPrice());
            return orderItem;
        }).collect(Collectors.toList());

        order.setItems(orderItems);
        
        Order savedOrder = orderRepository.save(order);
        
        // Clear cart
        cart.getItems().clear();
        cartRepository.save(cart);

        return mapToDTO(savedOrder);
    }
    
    public OrderHistoryDTO getUserOrders(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Order> orders = orderRepository.findByUserId(user.getId());
        
        OrderHistoryDTO response = new OrderHistoryDTO();
        
        OrderHistoryDTO.OrderStats stats = new OrderHistoryDTO.OrderStats();
        stats.setTotalOrders(orders.size());
        stats.setPending(orders.stream().filter(o -> o.getStatus() == OrderStatus.PENDING).count());
        stats.setCancelled(orders.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED).count());
        stats.setDelivered(orders.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).count());
        
        response.setStats(stats);
        response.setOrders(orders.stream().map(this::mapToDTO).collect(Collectors.toList()));
        
        return response;
    }

    private OrderDTO mapToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setItems(order.getItems().stream().map(item -> {
            OrderDTO.OrderItemDTO itemDto = new OrderDTO.OrderItemDTO();
            itemDto.setProductName(item.getProduct().getName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPrice(item.getPriceAtPurchase());
            return itemDto;
        }).collect(Collectors.toList()));
        return dto;
    }
}
