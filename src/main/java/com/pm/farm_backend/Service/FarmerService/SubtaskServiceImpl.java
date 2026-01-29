package com.pm.farm_backend.Service.FarmerService;

import com.pm.farm_backend.Model.CropCycle;
import com.pm.farm_backend.Model.FarmerProfile;
import com.pm.farm_backend.Model.Subtask;
import com.pm.farm_backend.Model.User;
import com.pm.farm_backend.Repositories.CropCycleRepository;
import com.pm.farm_backend.Repositories.FarmerProfileRepository;
import com.pm.farm_backend.Repositories.SubtaskRepository;
import com.pm.farm_backend.Repositories.UserRepository;
import com.pm.farm_backend.enums.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SubtaskServiceImpl implements SubtaskService {

    private final SubtaskRepository subtaskRepository;
    private final CropCycleRepository cropCycleRepository;
    private final FinanceService financeService;
    private final UserRepository userRepository;
    private final FarmerProfileRepository farmerProfileRepository;

    private FarmerProfile getFarmerByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return farmerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Farmer profile not found for user: " + email));
    }

    @Override
    public Subtask createSubtask(String email, Long cropCycleId, Subtask subtask) {
        FarmerProfile farmer = getFarmerByEmail(email);
        CropCycle cropCycle = cropCycleRepository.findById(cropCycleId)
                .orElseThrow(() -> new RuntimeException("CropCycle not found with id " + cropCycleId));

        if (!cropCycle.getFarmer().getId().equals(farmer.getId())) {
            throw new RuntimeException("Unauthorized: You do not own this crop cycle");
        }

        if (subtask.getStatus() == null) {
            subtask.setStatus(TaskStatus.PENDING);
        }
        if (subtask.getPriority() == null) {
            subtask.setPriority(com.pm.farm_backend.enums.TaskPriority.MEDIUM);
        }

        subtask.setCropCycle(cropCycle);
        subtask = subtaskRepository.save(subtask); // Save subtask explicitly to handle ID generation and persistence

        cropCycle.getSubtasks().add(subtask);

        // Recalculate total expense for the crop cycle from subtasks
        double totalExpense = cropCycle.getSubtasks().stream()
                .filter(t -> t.getExpense() != null && t.getStatus() != TaskStatus.DELETED)
                .mapToDouble(Subtask::getExpense)
                .sum();
        cropCycle.setTotalExpense(totalExpense);

        cropCycleRepository.save(cropCycle);

        // Auto-create expense transaction if expense > 0
        if (subtask.getExpense() != null && subtask.getExpense() > 0) {
            createExpenseTransaction(farmer.getUser(), cropCycle, subtask);
        }

        return subtask;
    }

    private void createExpenseTransaction(User user, CropCycle cropCycle, Subtask subtask) {
        try {
            com.pm.farm_backend.Dto.farmerDto.FinancialTransactionDTO tx = com.pm.farm_backend.Dto.farmerDto.FinancialTransactionDTO
                    .builder()
                    .amount(java.math.BigDecimal.valueOf(subtask.getExpense()))
                    .type(com.pm.farm_backend.enums.TransactionType.EXPENSE)
                    .description("Estimated cost for subtask: " + subtask.getTaskTitle())
                    .transactionDate(java.time.LocalDate.now())
                    .category("Crop Subtask")
                    .userId(user.getId())
                    .cropCycleId(cropCycle.getId())
                    .build();
            financeService.addTransaction(user.getEmail(), tx);
        } catch (Exception e) {
            System.err.println("Failed to auto-create expense: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Subtask> getSubtasksByCropId(String email, Long cropCycleId) {
        FarmerProfile farmer = getFarmerByEmail(email);
        CropCycle cropCycle = cropCycleRepository.findById(cropCycleId)
                .orElseThrow(() -> new RuntimeException("CropCycle not found"));

        if (!cropCycle.getFarmer().getId().equals(farmer.getId())) {
            throw new RuntimeException("Unauthorized: You do not own this crop cycle");
        }
        return subtaskRepository.findByCropCycleIdAndStatusNot(cropCycleId, TaskStatus.DELETED);
    }

    @Override
    public Subtask updateSubtask(String email, Long id, Subtask subtaskDetails) {
        FarmerProfile farmer = getFarmerByEmail(email);
        Subtask subtask = subtaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subtask not found with id " + id));

        if (!subtask.getCropCycle().getFarmer().getId().equals(farmer.getId())) {
            throw new RuntimeException("Unauthorized: You do not own this subtask");
        }

        subtask.setTaskTitle(subtaskDetails.getTaskTitle());
        subtask.setDescription(subtaskDetails.getDescription());
        subtask.setDueDate(subtaskDetails.getDueDate());
        subtask.setPriority(subtaskDetails.getPriority());
        subtask.setExpense(subtaskDetails.getExpense());

        Subtask updatedSubtask = subtaskRepository.save(subtask);

        // Recalculate total expense properly after save
        CropCycle cropCycle = subtask.getCropCycle();
        if (cropCycle != null) {
            double totalExpense = cropCycle.getSubtasks().stream()
                    .filter(t -> t.getExpense() != null && t.getStatus() != TaskStatus.DELETED)
                    .mapToDouble(Subtask::getExpense)
                    .sum();
            cropCycle.setTotalExpense(totalExpense);
            cropCycleRepository.save(cropCycle);
        }

        return updatedSubtask;
    }

    @Override
    public Subtask updateSubtaskStatus(String email, Long id, TaskStatus status) {
        FarmerProfile farmer = getFarmerByEmail(email);
        Subtask subtask = subtaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subtask not found with id " + id));

        if (!subtask.getCropCycle().getFarmer().getId().equals(farmer.getId())) {
            throw new RuntimeException("Unauthorized: You do not own this subtask");
        }

        subtask.setStatus(status);
        return subtaskRepository.save(subtask);
    }

    @Override
    public void deleteSubtask(String email, Long id) {
        FarmerProfile farmer = getFarmerByEmail(email);
        Subtask subtask = subtaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subtask not found with id " + id));

        if (!subtask.getCropCycle().getFarmer().getId().equals(farmer.getId())) {
            throw new RuntimeException("Unauthorized: You do not own this subtask");
        }

        subtask.setStatus(TaskStatus.DELETED);
        subtaskRepository.save(subtask);
    }
}
