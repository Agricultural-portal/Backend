package com.pm.farm_backend.Service.FarmerService;

import com.pm.farm_backend.Dto.farmerDto.DashboardStatsDTO;
import com.pm.farm_backend.Model.Subtask;
import com.pm.farm_backend.Repositories.CropCycleRepository;
import com.pm.farm_backend.Repositories.GovSchemeRepository;
import com.pm.farm_backend.Repositories.OrderRepository;
import com.pm.farm_backend.Repositories.SubtaskRepository;
import com.pm.farm_backend.Repositories.FarmerProfileRepository;
import com.pm.farm_backend.enums.CropStatus;
import com.pm.farm_backend.enums.OrderStatus;
import com.pm.farm_backend.enums.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FarmerDashboardServiceImpl implements FarmerDashboardService {

    private final SubtaskRepository subtaskRepository;
    private final CropCycleRepository cropCycleRepository;
    private final OrderRepository orderRepository;
    private final GovSchemeRepository govSchemeRepository;
    private final FarmerProfileRepository farmerProfileRepository;
    private final com.pm.farm_backend.Repositories.TaskRepository taskRepository;
    private final FinanceService financeService;
    private final OrderService orderService;

    @Override
    public DashboardStatsDTO getStats(String email) {
        // Resolve Farmer from Email
        // Resolve Farmer from Email
        com.pm.farm_backend.Model.FarmerProfile farmer = farmerProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Farmer profile not found for email: " + email));

        Long farmerId = farmer.getId();
        com.pm.farm_backend.Model.User user = farmer.getUser();
        Long userId = user.getId();

        System.out.println("DEBUG: DashboardStats - Email: " + email + " -> Resolved FarmerId: " + farmerId);

        System.out.println("DEBUG: DashboardStats - Email: " + email + " -> Resolved FarmerId: " + farmerId);

        // Count Subtasks (Crop Cycle related)
        Integer subtasksCount = subtaskRepository.countByCropCycleFarmerId(farmerId);
        if (subtasksCount == null)
            subtasksCount = 0;

        // Count General Tasks (My Tasks)
        long generalTasksCount = taskRepository.countByFarmer_Id(farmerId);

        long totalTasks = subtasksCount + generalTasksCount;

        // Completed Subtasks
        Integer completedSubtasks = subtaskRepository.countByCropCycleFarmerIdAndStatus(farmerId, TaskStatus.COMPLETED);
        if (completedSubtasks == null)
            completedSubtasks = 0;

        // Completed General Tasks
        long completedGeneralTasks = taskRepository.countByFarmer_IdAndStatus(farmerId, TaskStatus.COMPLETED);

        long completedTasks = completedSubtasks + completedGeneralTasks;

        Integer activeCrops = cropCycleRepository.countByFarmerIdAndStatus(farmerId, CropStatus.IN_PROGRESS);

        // Use OrderService to ensure consistency with "My Orders" page
        List<com.pm.farm_backend.Dto.farmerDto.FarmerOrderItemDTO> farmerOrders = orderService
                .getAllOrdersForFarmer(email);
        long pendingOrdersCount = farmerOrders.stream()
                .filter(o -> "PENDING".equalsIgnoreCase(o.getStatus()))
                .map(com.pm.farm_backend.Dto.farmerDto.FarmerOrderItemDTO::getOrderId)
                .distinct()
                .count();
        Integer pendingOrders = (int) pendingOrdersCount;
        long schemes = govSchemeRepository.count();

        // Fetch Financial Stats
        java.math.BigDecimal totalIncome = java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalExpense = java.math.BigDecimal.ZERO;
        java.math.BigDecimal netProfit = java.math.BigDecimal.ZERO;

        if (email != null) {
            java.util.Map<String, Object> financeSummary = financeService.getFinancialSummary(email);
            totalIncome = (java.math.BigDecimal) financeSummary.get("totalIncome");
            totalExpense = (java.math.BigDecimal) financeSummary.get("totalExpense");
            netProfit = (java.math.BigDecimal) financeSummary.get("netProfit");
        }

        return DashboardStatsDTO.builder()
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .activeCropCycles(activeCrops != null ? activeCrops : 0)
                .pendingOrders(pendingOrders != null ? pendingOrders : 0)
                .eligibleSchemes(schemes)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netProfit(netProfit)
                .build();
    }

    @Override
    public List<com.pm.farm_backend.Dto.farmerDto.RecentTaskDTO> getRecentTasks(String email) {
        com.pm.farm_backend.Model.FarmerProfile farmer = farmerProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Farmer profile not found for email: " + email));
        Long farmerId = farmer.getId();

        // Fetch top 5 subtasks
        Page<Subtask> subtasksPage = subtaskRepository.findByCropCycleFarmerIdOrderByDueDateAsc(
                farmerId,
                PageRequest.of(0, 5));
        List<Subtask> subtasks = subtasksPage.getContent();

        // Fetch top 5 general tasks
        List<com.pm.farm_backend.Model.Task> tasks = taskRepository.findByFarmer(farmer);
        if (tasks == null)
            tasks = java.util.Collections.emptyList();

        List<com.pm.farm_backend.Dto.farmerDto.RecentTaskDTO> combined = new java.util.ArrayList<>();

        // Map Subtasks
        for (Subtask s : subtasks) {
            combined.add(com.pm.farm_backend.Dto.farmerDto.RecentTaskDTO.builder()
                    .id(s.getId())
                    .title(s.getTaskTitle())
                    .status(s.getStatus().toString())
                    .dueDate(s.getDueDate())
                    .expense(s.getExpense())
                    .type("Crop Cycle")
                    .build());
        }

        // Map Tasks
        for (com.pm.farm_backend.Model.Task t : tasks) {
            combined.add(com.pm.farm_backend.Dto.farmerDto.RecentTaskDTO.builder()
                    .id(t.getTaskId())
                    .title(t.getName())
                    .status(t.getStatus().toString())
                    // Task model has dueDate method? Yes.
                    .dueDate(t.getDueDate())
                    .expense(t.getExpense())
                    .type("General")
                    .build());
        }

        // Sort by DueDate ascending
        combined.sort((t1, t2) -> {
            if (t1.getDueDate() == null)
                return 1;
            if (t2.getDueDate() == null)
                return -1;
            return t1.getDueDate().compareTo(t2.getDueDate());
        });

        // Return top 5
        return combined.stream().limit(5).collect(java.util.stream.Collectors.toList());
    }
}
