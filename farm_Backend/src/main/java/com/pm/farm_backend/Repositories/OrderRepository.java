package com.pm.farm_backend.Repositories;

import com.pm.farm_backend.Model.Order;
import com.pm.farm_backend.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o")
    BigDecimal sumTotalAmount();
    
    long countByStatus(OrderStatus status);
    
    java.util.List<Order> findByUserId(Long userId);
}
