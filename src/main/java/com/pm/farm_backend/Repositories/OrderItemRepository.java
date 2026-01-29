package com.pm.farm_backend.Repositories;

import com.pm.farm_backend.Model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.product JOIN FETCH oi.order WHERE oi.product.id = :productId")
    List<OrderItem> findByProductId(Long productId);

    // Find all order items for products belonging to a specific farmer
    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.product JOIN FETCH oi.order WHERE oi.product.farmer.id = :farmerId")
    List<OrderItem> findByProductFarmerId(Long farmerId);
}
