package com.pm.farm_backend.Dto.farmerDto;

import com.pm.farm_backend.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialTransactionDTO {
    private Long id;
    private BigDecimal amount;
    private TransactionType type;
    private String description;
    private LocalDate transactionDate;
    private String category;

    private Long cropCycleId;
    private String cropName; // Helper for UI

    private Long orderId;

    private Long userId;
}
