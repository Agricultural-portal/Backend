package com.pm.farm_backend.Service.FarmerService;

import com.pm.farm_backend.Model.Order;
import com.pm.farm_backend.Model.OrderItem;
import com.pm.farm_backend.Repositories.OrderRepository;
import com.pm.farm_backend.Repositories.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final com.pm.farm_backend.Repositories.ProductRepository productRepository;
    private final FinanceService financeService;
    private final com.pm.farm_backend.Repositories.UserRepository userRepository;

    @Override
    public List<com.pm.farm_backend.Dto.farmerDto.FarmerOrderItemDTO> getOrdersForProduct(Long productId) {
        List<OrderItem> items = orderItemRepository.findByProductId(productId);
        return items.stream().map(item -> com.pm.farm_backend.Dto.farmerDto.FarmerOrderItemDTO.builder()
                .id(item.getId())
                .orderId(item.getOrder().getId())
                .productName(item.getProduct().getName())
                .productUnit(item.getProduct().getUnit().toString())
                .productImageUrl(item.getProduct().getImageUrl())
                .buyerName(
                        item.getOrder().getUser() != null ? item.getOrder().getUser().getFullName() : "Unknown Buyer")
                .quantity(item.getQuantity())
                .priceAtPurchase(item.getPriceAtPurchase())
                .totalAmount(item.getPriceAtPurchase().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))
                .orderDate(item.getOrder().getCreatedAt())
                .status(item.getOrder().getStatus().toString())
                .paymentStatus(item.getOrder().getPaymentStatus())
                .build())
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<com.pm.farm_backend.Dto.farmerDto.FarmerOrderItemDTO> getAllOrdersForFarmer(String email) {
        com.pm.farm_backend.Model.User farmer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        if (farmer.getRole() != com.pm.farm_backend.enums.Role.FARMER) {
            throw new RuntimeException("User is not a farmer");
        }

        List<OrderItem> items = orderItemRepository.findByProductFarmerId(farmer.getId());
        return items.stream().map(item -> com.pm.farm_backend.Dto.farmerDto.FarmerOrderItemDTO.builder()
                .id(item.getId())
                .orderId(item.getOrder().getId())
                .productName(item.getProduct().getName())
                .productUnit(item.getProduct().getUnit().toString())
                .productImageUrl(item.getProduct().getImageUrl())
                .buyerName(
                        item.getOrder().getUser() != null ? item.getOrder().getUser().getFullName() : "Unknown Buyer")
                .quantity(item.getQuantity())
                .priceAtPurchase(item.getPriceAtPurchase())
                .totalAmount(item.getPriceAtPurchase().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))
                .orderDate(item.getOrder().getCreatedAt())
                .status(item.getOrder().getStatus().toString())
                .paymentStatus(item.getOrder().getPaymentStatus())
                .build())
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id " + id));
    }

    @Override
    public void updateOrderStatus(Long orderId, String status) {
        Order order = getOrderById(orderId);
        try {
            com.pm.farm_backend.enums.OrderStatus newStatus = com.pm.farm_backend.enums.OrderStatus
                    .valueOf(status.toUpperCase());
            order.setStatus(newStatus);
            orderRepository.save(order);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order status: " + status);
        }
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public Order placeOrder(Order order) {
        // Ensure items link back to order
        if (order.getItems() != null) {
            order.getItems().forEach(item -> item.setOrder(order));
        }

        if (order.getPaymentStatus() != null) {
            order.setPaymentStatus(order.getPaymentStatus().trim());
        }

        Order savedOrder = orderRepository.save(order);

        // Auto-create income transactions for farmers ONLY if payment is already PAID
        // (e.g. immediate online payment)
        if ("PAID".equalsIgnoreCase(savedOrder.getPaymentStatus())) {
            createIncomeForOrder(savedOrder);
        }

        return savedOrder;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public String forceCreateIncome(Long orderId) {
        StringBuilder logs = new StringBuilder();
        try {
            logs.append("Fetching Order: ").append(orderId).append("\n");
            Order order = getOrderById(orderId);
            logs.append("Order Found. Status: ").append(order.getStatus()).append(", Payment: ")
                    .append(order.getPaymentStatus()).append("\n");

            if (order.getItems() == null || order.getItems().isEmpty()) {
                logs.append("WARNING: Order has NO items.\n");
            } else {
                logs.append("Order has ").append(order.getItems().size()).append(" items.\n");
            }

            createIncomeForOrder(order);
            logs.append("Income creation triggered successfully.\n");
        } catch (Exception e) {
            logs.append("ERROR: ").append(e.getMessage()).append("\n");
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.PrintWriter pw = new java.io.PrintWriter(sw);
            e.printStackTrace(pw);
            logs.append(sw.toString());
        }
        return logs.toString();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void updatePaymentStatus(Long orderId, String paymentStatus) {
        Order order = getOrderById(orderId);
        String oldStatus = order.getPaymentStatus();
        String newStatus = paymentStatus != null ? paymentStatus.trim() : null;
        order.setPaymentStatus(newStatus);

        Order savedOrder = orderRepository.save(order);

        // If status changed to PAID, trigger income creation
        if ("PAID".equalsIgnoreCase(newStatus) && !"PAID".equalsIgnoreCase(oldStatus)) {
            createIncomeForOrder(savedOrder);
        }
    }

    private void createIncomeForOrder(Order order) {
        System.out.println("DEBUG: createIncomeForOrder called for Order ID: " + order.getId());
        if (order.getItems() != null) {
            System.out.println("DEBUG: Order has " + order.getItems().size() + " items.");
            for (OrderItem item : order.getItems()) {
                try {
                    System.out.println(
                            "DEBUG: Processing item: " + item.getId() + ", Product ID: " + item.getProduct().getId());
                    // Fetch product to ensure we get the farmer details
                    com.pm.farm_backend.Model.Product product = productRepository.findById(item.getProduct().getId())
                            .orElse(null);

                    if (product != null) {
                        System.out.println("DEBUG: Product found: " + product.getName() + ", Farmer: "
                                + (product.getFarmer() != null ? product.getFarmer().getId() : "null"));
                    } else {
                        System.out.println("DEBUG: Product not found for ID: " + item.getProduct().getId());
                    }

                    if (product != null && product.getFarmer() != null) {
                        java.math.BigDecimal incomeAmount = item.getPriceAtPurchase()
                                .multiply(new java.math.BigDecimal(item.getQuantity()));

                        System.out.println("DEBUG: Creating transaction amount: " + incomeAmount);

                        com.pm.farm_backend.Dto.farmerDto.FinancialTransactionDTO tx = com.pm.farm_backend.Dto.farmerDto.FinancialTransactionDTO
                                .builder()
                                .amount(incomeAmount)
                                .type(com.pm.farm_backend.enums.TransactionType.INCOME)
                                .description("Income from Order #" + order.getId() + " - " + product.getName())
                                .transactionDate(java.time.LocalDate.now())
                                .category("Product Sale")
                                .userId(product.getFarmer().getId())
                                .orderId(order.getId())
                                .build();

                        // FIX: Pass farmer's email to addTransaction
                        String farmerEmail = product.getFarmer().getEmail();
                        financeService.addTransaction(farmerEmail, tx);

                        System.out.println("DEBUG: Transaction created successfully.");
                    }
                } catch (Exception e) {
                    System.err.println("Failed to auto-create income: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("DEBUG: Order items are null for Order ID: " + order.getId());
        }
    }
}
