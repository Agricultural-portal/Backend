package com.pm.farm_backend.Service.buyerService;

import com.pm.farm_backend.Model.*;
import com.pm.farm_backend.Dto.CreateNotificationDto;
import com.pm.farm_backend.Dto.buyerDTO.OrderDTO;
import com.pm.farm_backend.Dto.buyerDTO.PlaceOrderRequest;
import com.pm.farm_backend.Dto.buyerDTO.OrderHistoryDTO;
import com.pm.farm_backend.Service.NotificationService;
import com.pm.farm_backend.enums.NotificationPriority;
import com.pm.farm_backend.enums.NotificationType;
import com.pm.farm_backend.enums.OrderStatus;
import com.pm.farm_backend.Repositories.CartRepository;
import com.pm.farm_backend.Repositories.OrderRepository;
import com.pm.farm_backend.Repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationService notificationService;

    @Transactional
    public OrderDTO placeOrder(PlaceOrderRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Calculate order total
        BigDecimal orderTotal = request.getTotalAmount() != null ? request.getTotalAmount() : cart.getTotalAmount();

        // Check wallet balance
        BigDecimal buyerBalance = user.getMoney() != null ? user.getMoney() : BigDecimal.ZERO;
        if (buyerBalance.compareTo(orderTotal) < 0) {
            throw new RuntimeException("Insufficient wallet balance. Please add money to your wallet.");
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

        // Create notification for buyer
        try {
            CreateNotificationDto notificationDto = new CreateNotificationDto();
            notificationDto.setUserId(user.getId());
            notificationDto.setType(NotificationType.ORDER);
            notificationDto.setTitle("Order Placed Successfully");
            notificationDto.setMessage("Your order #" + savedOrder.getId() + " has been placed successfully. Total: ₹" + orderTotal);
            notificationDto.setPriority(NotificationPriority.HIGH);
            notificationService.createNotification(notificationDto);
            log.info("Notification created for order: {}", savedOrder.getId());
        } catch (Exception e) {
            log.error("Failed to create notification for order: {}", e.getMessage());
        }

        // Create notification for each farmer whose product was ordered
        try {
            savedOrder.getItems().stream()
                .map(item -> item.getProduct().getFarmer())
                .distinct()
                .forEach(farmer -> {
                    try {
                        CreateNotificationDto farmerNotification = new CreateNotificationDto();
                        farmerNotification.setUserId(farmer.getId());
                        farmerNotification.setType(NotificationType.ORDER);
                        farmerNotification.setTitle("New Order Received");
                        farmerNotification.setMessage("You have received a new order from " + user.getFirst_name() + " " + user.getLast_name());
                        farmerNotification.setPriority(NotificationPriority.HIGH);
                        notificationService.createNotification(farmerNotification);
                    } catch (Exception e) {
                        log.error("Failed to create notification for farmer: {}", e.getMessage());
                    }
                });
        } catch (Exception e) {
            log.error("Failed to create farmer notifications: {}", e.getMessage());
        }

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

    @Autowired
    private com.pm.farm_backend.Repositories.RatingRepository ratingRepository;

    private OrderDTO mapToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setCreatedAt(order.getCreatedAt());

        // Check for rating
        ratingRepository.findByOrderId(order.getId()).ifPresentOrElse(
                rating -> {
                    dto.setRated(true);
                    dto.setRating(rating.getStars());
                },
                () -> {
                    dto.setRated(false);
                    dto.setRating(null);
                });

        dto.setItems(order.getItems().stream().map(item -> {
            OrderDTO.OrderItemDTO itemDto = new OrderDTO.OrderItemDTO();
            itemDto.setProductId(item.getProduct().getId());
            itemDto.setProductName(item.getProduct().getName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPrice(item.getPriceAtPurchase());
            return itemDto;
        }).collect(Collectors.toList()));
        return dto;
    }
}
