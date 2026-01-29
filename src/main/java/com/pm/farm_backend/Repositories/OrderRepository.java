package com.pm.farm_backend.Repositories;

import com.pm.farm_backend.Model.Order;
import com.pm.farm_backend.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT SUM(o.totalAmount) FROM Order o")
    BigDecimal sumTotalAmount();

    long countByStatus(OrderStatus status);

    List<Order> findByUserId(Long userId);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.items oi WHERE oi.product.farmer.id = :farmerId")
    List<Order> findByFarmerId(@Param("farmerId") Long farmerId);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.items oi WHERE oi.product.farmer.id = :farmerId AND o.status = :status")
    List<Order> findByFarmerIdAndStatus(@Param("farmerId") Long farmerId, @Param("status") OrderStatus status);

    Integer countByUserIdAndStatus(Long userId, com.pm.farm_backend.enums.OrderStatus status);
}
