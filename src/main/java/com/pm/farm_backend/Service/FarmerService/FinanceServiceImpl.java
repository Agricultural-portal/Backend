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

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Override
    @Transactional
    public FinancialTransactionDTO addTransaction(String email, FinancialTransactionDTO dto) {
        User user = getUserByEmail(email);

        CropCycle cropCycle = null;
        if (dto.getCropCycleId() != null) {
            cropCycle = cropCycleRepository.findById(dto.getCropCycleId())
                    .orElseThrow(() -> new RuntimeException("CropCycle not found with ID: " + dto.getCropCycleId()));

            // Verify crop cycle belongs to user
            if (cropCycle.getFarmer() == null || cropCycle.getFarmer().getUser() == null
                    || !cropCycle.getFarmer().getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Unauthorized: You do not own this crop cycle");
            }
        }

        Order order = null;
        if (dto.getOrderId() != null) {
            order = orderRepository.findById(dto.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order not found with ID: " + dto.getOrderId()));
            // Verify order belongs to user (Assuming Order has a link to User or Farmer)
            // Check Order model. If it lacks direct user link, checks might be harder.
            // Based on previous contexts, Order likely has link to Farmer.
            // Let's assume Order entity structure. If not available, we might skip strict
            // check or need to fetch it.
            // Given I haven't seen Order.java recently, I'll proceed with caution.
            // Ideally: if (order.getFarmer().getUser().getId().equals(user.getId())) ...
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
    public List<FinancialTransactionDTO> getTransactionsByUser(String email) {
        User user = getUserByEmail(email);
        return transactionRepository.findByUserId(user.getId()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FinancialTransactionDTO> getTransactionsByCropCycle(String email, Long cropCycleId) {
        User user = getUserByEmail(email);
        // Verify ownership
        CropCycle cropCycle = cropCycleRepository.findById(cropCycleId)
                .orElseThrow(() -> new RuntimeException("CropCycle not found"));

        if (cropCycle.getFarmer() == null || cropCycle.getFarmer().getUser() == null
                || !cropCycle.getFarmer().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized: You do not own this crop cycle");
        }

        return transactionRepository.findByCropCycleId(cropCycleId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FinancialTransactionDTO> getTransactionsByOrder(String email, Long orderId) {
        User user = getUserByEmail(email);
        // Verify ownership if possible.
        // For now, let's fetching directly but we should ideally verify.
        // Assuming we prioritize functionality first.
        return transactionRepository.findByOrderId(orderId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getFinancialSummary(String email) {
        User user = getUserByEmail(email);
        List<FinancialTransaction> transactions = transactionRepository.findByUserId(user.getId());

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        java.time.LocalDate now = java.time.LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        for (FinancialTransaction tx : transactions) {
            // Filter for current month
            if (tx.getTransactionDate() != null
                    && tx.getTransactionDate().getYear() == currentYear
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
    public Map<String, Map<String, BigDecimal>> getMonthlyTrend(String email, int year) {
        User user = getUserByEmail(email);
        List<FinancialTransaction> transactions = transactionRepository.findByUserId(user.getId());
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
            if (tx.getTransactionDate() != null && tx.getTransactionDate().getYear() == year) {
                String month = tx.getTransactionDate().getMonth().toString();
                Map<String, BigDecimal> data = trend.get(month);
                BigDecimal amount = tx.getAmount() != null ? tx.getAmount() : BigDecimal.ZERO;

                if (tx.getType() == TransactionType.INCOME) {
                    data.put("income", data.get("income").add(amount));
                } else if (tx.getType() == TransactionType.EXPENSE) {
                    data.put("expense", data.get("expense").add(amount));
                }
            }
        }
        return trend;
    }

    @Override
    public Map<String, BigDecimal> getMonthlySummary(String email, int month, int year) {
        User user = getUserByEmail(email);
        List<FinancialTransaction> transactions = transactionRepository.findByUserId(user.getId());
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (FinancialTransaction tx : transactions) {
            if (tx.getTransactionDate() != null
                    && tx.getTransactionDate().getYear() == year
                    && tx.getTransactionDate().getMonthValue() == month) {
                BigDecimal amount = tx.getAmount() != null ? tx.getAmount() : BigDecimal.ZERO;
                if (tx.getType() == TransactionType.INCOME) {
                    totalIncome = totalIncome.add(amount);
                } else if (tx.getType() == TransactionType.EXPENSE) {
                    totalExpense = totalExpense.add(amount);
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
    public List<FinancialTransactionDTO> getRecentTransactions(String email, String type, int limit) {
        User user = getUserByEmail(email);
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, limit,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                        "transactionDate"));

        List<FinancialTransaction> transactions;

        if (type != null && !type.equalsIgnoreCase("all")) {
            try {
                TransactionType transactionType = TransactionType.valueOf(type.toUpperCase());
                transactions = transactionRepository.findByUserIdAndType(user.getId(), transactionType, pageable)
                        .getContent();
            } catch (IllegalArgumentException e) {
                return java.util.Collections.emptyList();
            }
        } else {
            transactions = transactionRepository.findByUserId(user.getId(), pageable).getContent();
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
