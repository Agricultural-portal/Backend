package com.pm.farm_backend.Controller.BuyerController;

import com.pm.farm_backend.Dto.buyerDTO.OrderDTO;
import com.pm.farm_backend.Dto.buyerDTO.PlaceOrderRequest;
import com.pm.farm_backend.Dto.buyerDTO.OrderHistoryDTO;
import com.pm.farm_backend.Service.buyerService.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buyer/orders")
@PreAuthorize("hasRole('BUYER')")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place")
    public OrderDTO placeOrder(@RequestBody PlaceOrderRequest request, Authentication authentication) {
        return orderService.placeOrder(request, authentication.getName());
    }

    @GetMapping
    public OrderHistoryDTO getUserOrders(Authentication authentication) {
        return orderService.getUserOrders(authentication.getName());
    }
}
