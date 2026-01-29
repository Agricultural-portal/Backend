package com.pm.farm_backend.Controller.FarmerController;

import com.pm.farm_backend.Service.FarmerService.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FARMER')")
public class FarmerOrderController {

    private final OrderService orderService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<com.pm.farm_backend.Dto.farmerDto.FarmerOrderItemDTO>> getOrdersByProduct(
            @PathVariable Long productId) {
        return ResponseEntity.ok(orderService.getOrdersForProduct(productId));
    }

    @GetMapping
    public ResponseEntity<List<com.pm.farm_backend.Dto.farmerDto.FarmerOrderItemDTO>> getOrdersForFarmer(
            java.security.Principal principal) {
        return ResponseEntity.ok(orderService.getAllOrdersForFarmer(principal.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<com.pm.farm_backend.Model.Order> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping("/place")
    public ResponseEntity<com.pm.farm_backend.Model.Order> placeOrder(
            @RequestBody com.pm.farm_backend.Model.Order order) {
        return ResponseEntity.ok(orderService.placeOrder(order));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/payment-status")
    public ResponseEntity<Void> updatePaymentStatus(@PathVariable Long id, @RequestParam String status) {
        orderService.updatePaymentStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/debug-income/{id}")
    public ResponseEntity<String> debugIncome(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.forceCreateIncome(id));
    }
}
