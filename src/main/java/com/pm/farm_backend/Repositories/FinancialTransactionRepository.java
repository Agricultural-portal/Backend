package com.pm.farm_backend.Repositories;

import com.pm.farm_backend.Model.FinancialTransaction;
import com.pm.farm_backend.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, Long> {

        List<FinancialTransaction> findByUserId(Long userId);

        List<FinancialTransaction> findByCropCycleId(Long cropCycleId);

        List<FinancialTransaction> findByOrderId(Long orderId);

        List<FinancialTransaction> findByUserIdAndType(Long userId, TransactionType type);

        // Recent transactions support
        org.springframework.data.domain.Page<FinancialTransaction> findByUserId(Long userId,
                        org.springframework.data.domain.Pageable pageable);

        org.springframework.data.domain.Page<FinancialTransaction> findByUserIdAndType(Long userId,
                        TransactionType type,
                        org.springframework.data.domain.Pageable pageable);

        // To get expenses for a crop cycle (implied by linking)
        // To get income for an order
}
