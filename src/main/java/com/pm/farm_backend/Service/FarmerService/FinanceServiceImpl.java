package com.pm.farm_backend.Service.FarmerService;

import com.pm.farm_backend.Dto.farmerDto.FinancialTransactionDTO;
import com.pm.farm_backend.Model.CropCycle;
import com.pm.farm_backend.Model.FinancialTransaction;
import com.pm.farm_backend.Model.Order;
import com.pm.farm_backend.Model.User;
import com.pm.farm_backend.Repositories.CropCycleRepository;
import com.pm.farm_backend.Repositories.FinancialTransactionRepository;
import com.pm.farm_backend.Repositories.OrderRepository;
import com.pm.farm_backend.Repositories.UserRepository;
import com.pm.farm_backend.enums.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FinanceServiceImpl implements FinanceService {

    @Autowired
    private FinancialTransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CropCycleRepository cropCycleRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    @Transactional
    public FinancialTransactionDTO addTransaction(FinancialTransactionDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + dto.getUserId()));

        CropCycle cropCycle = null;
        if (dto.getCropCycleId() != null) {
            cropCycle = cropCycleRepository.findById(dto.getCropCycleId())
                    .orElseThrow(() -> new RuntimeException("CropCycle not found with ID: " + dto.getCropCycleId()));
        }

        Order order = null;
        if (dto.getOrderId() != null) {
            order = orderRepository.findById(dto.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order not found with ID: " + dto.getOrderId()));
        }

        FinancialTransaction transaction = FinancialTransaction.builder()
                .amount(dto.getAmount())
                .type(dto.getType())
                .description(dto.getDescription())
                .transactionDate(dto.getTransactionDate())
                .category(dto.getCategory())
                .user(user)
                .cropCycle(cropCycle)
                .order(order)
                .build();

        FinancialTransaction saved = transactionRepository.save(transaction);
        return mapToDTO(saved);
    }

    @Override
    public List<FinancialTransactionDTO> getTransactionsByUser(Long userId) {
        return transactionRepository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FinancialTransactionDTO> getTransactionsByCropCycle(Long cropCycleId) {
        return transactionRepository.findByCropCycleId(cropCycleId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FinancialTransactionDTO> getTransactionsByOrder(Long orderId) {
        return transactionRepository.findByOrderId(orderId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getFinancialSummary(Long userId) {
        List<FinancialTransaction> transactions = transactionRepository.findByUserId(userId);

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        java.time.LocalDate now = java.time.LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        for (FinancialTransaction tx : transactions) {
            // Filter for current month
            if (tx.getTransactionDate().getYear() == currentYear
                    && tx.getTransactionDate().getMonthValue() == currentMonth) {
                if (tx.getType() == TransactionType.INCOME) {
                    totalIncome = totalIncome.add(tx.getAmount());
                } else if (tx.getType() == TransactionType.EXPENSE) {
                    totalExpense = totalExpense.add(tx.getAmount());
                }
            }
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalIncome", totalIncome);
        summary.put("totalExpense", totalExpense);

        BigDecimal net = totalIncome.subtract(totalExpense);
        summary.put("netProfit", net);

        if (net.compareTo(BigDecimal.ZERO) > 0) {
            summary.put("status", "Profit");
        } else if (net.compareTo(BigDecimal.ZERO) < 0) {
            summary.put("status", "Loss");
        } else {
            summary.put("status", "Break-even");
        }

        return summary;
    }

    @Override
    public Map<String, Map<String, BigDecimal>> getMonthlyTrend(Long userId, int year) {
        List<FinancialTransaction> transactions = transactionRepository.findByUserId(userId);
        Map<String, Map<String, BigDecimal>> trend = new java.util.LinkedHashMap<>(); // LinkedHashMap for order

        String[] months = { "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY",
                "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER" };

        for (String month : months) {
            Map<String, BigDecimal> data = new HashMap<>();
            data.put("income", BigDecimal.ZERO);
            data.put("expense", BigDecimal.ZERO);
            trend.put(month, data);
        }

        for (FinancialTransaction tx : transactions) {
            if (tx.getTransactionDate().getYear() == year) {
                String month = tx.getTransactionDate().getMonth().toString();
                Map<String, BigDecimal> data = trend.get(month);
                if (tx.getType() == TransactionType.INCOME) {
                    data.put("income", data.get("income").add(tx.getAmount()));
                } else if (tx.getType() == TransactionType.EXPENSE) {
                    data.put("expense", data.get("expense").add(tx.getAmount()));
                }
            }
        }
        return trend;
    }

    @Override
    public Map<String, BigDecimal> getMonthlySummary(Long userId, int month, int year) {
        List<FinancialTransaction> transactions = transactionRepository.findByUserId(userId);
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (FinancialTransaction tx : transactions) {
            if (tx.getTransactionDate().getYear() == year && tx.getTransactionDate().getMonthValue() == month) {
                if (tx.getType() == TransactionType.INCOME) {
                    totalIncome = totalIncome.add(tx.getAmount());
                } else if (tx.getType() == TransactionType.EXPENSE) {
                    totalExpense = totalExpense.add(tx.getAmount());
                }
            }
        }

        Map<String, BigDecimal> summary = new HashMap<>();
        summary.put("totalIncome", totalIncome);
        summary.put("totalExpense", totalExpense);
        summary.put("netProfit", totalIncome.subtract(totalExpense));
        return summary;
    }

    @Override
    public List<FinancialTransactionDTO> getRecentTransactions(Long userId, String type, int limit) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, limit,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                        "transactionDate"));

        List<FinancialTransaction> transactions;

        if (type != null && !type.equalsIgnoreCase("all")) {
            try {
                TransactionType transactionType = TransactionType.valueOf(type.toUpperCase());
                transactions = transactionRepository.findByUserIdAndType(userId, transactionType, pageable)
                        .getContent();
            } catch (IllegalArgumentException e) {
                // Invalid type, default to all? or empty?
                // For safety, fallback to all or throw error.
                // User requirement implies valid input. If invalid, maybe just return empty or
                // all.
                // Let's return empty to indicate error in filter.
                return java.util.Collections.emptyList();
            }
        } else {
            transactions = transactionRepository.findByUserId(userId, pageable).getContent();
        }

        return transactions.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private FinancialTransactionDTO mapToDTO(FinancialTransaction tx) {
        return FinancialTransactionDTO.builder()
                .id(tx.getId())
                .amount(tx.getAmount())
                .type(tx.getType())
                .description(tx.getDescription())
                .transactionDate(tx.getTransactionDate())
                .category(tx.getCategory())
                .userId(tx.getUser().getId())
                .cropCycleId(tx.getCropCycle() != null ? tx.getCropCycle().getId() : null)
                .cropName(tx.getCropCycle() != null ? tx.getCropCycle().getCropName() : null)
                .orderId(tx.getOrder() != null ? tx.getOrder().getId() : null)
                .build();
    }
}
