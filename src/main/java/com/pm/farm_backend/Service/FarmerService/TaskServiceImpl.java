package com.pm.farm_backend.Service.FarmerService;

import com.pm.farm_backend.Dto.CreateNotificationDto;
import com.pm.farm_backend.Dto.farmerDto.CreateTaskRequest;
import com.pm.farm_backend.Dto.farmerDto.TaskResponse;
import com.pm.farm_backend.Model.FarmerProfile;
import com.pm.farm_backend.Model.Task;
import com.pm.farm_backend.Model.User;
import com.pm.farm_backend.Repositories.FarmerProfileRepository;
import com.pm.farm_backend.Repositories.TaskRepository;
import com.pm.farm_backend.Repositories.UserRepository;
import com.pm.farm_backend.Service.NotificationService;
import com.pm.farm_backend.enums.Category;
import com.pm.farm_backend.enums.NotificationPriority;
import com.pm.farm_backend.enums.NotificationType;
import com.pm.farm_backend.enums.TaskStatus;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

        @Autowired
        private TaskRepository taskRepository;
        @Autowired
        private FarmerProfileRepository farmerProfileRepository;
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private com.pm.farm_backend.Repositories.CropCycleRepository cropCycleRepository;
        @Autowired
        private NotificationService notificationService;
        private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TaskServiceImpl.class);

        @Autowired
        private com.pm.farm_backend.Service.FarmerService.FinanceService financeService;

        @Override
        public Map<String, Object> getTaskProgressByFarmer(String email) {

                FarmerProfile farmer = getFarmerByEmail(email);
                Long farmerId = farmer.getId();

                long totalTasks = taskRepository.countByFarmer_Id(farmerId);
                long completedTasks = taskRepository.countByFarmer_IdAndStatus(
                                farmerId, TaskStatus.COMPLETED);

                int progress = totalTasks == 0 ? 0 : (int) ((completedTasks * 100) / totalTasks);

                Map<String, Object> response = new HashMap<>();
                response.put("farmerId", farmerId);
                response.put("totalTasks", totalTasks);
                response.put("completedTasks", completedTasks);
                response.put("progressPercentage", progress);

                return response;

        }

        @Override
        public Task createTaskForFarmer(CreateTaskRequest request, String email) {

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

                FarmerProfile farmer = farmerProfileRepository.findByUser(user)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Farmer profile not found for user: " + email));

                Task task = new Task();
                task.setName(request.getName());
                task.setDescription(request.getDescription());
                task.setFarmer(farmer);
                task.setCategory(request.getCategory());
                task.setDueDate(request.getDueDate());
                task.setExpense(request.getExpense());
                task.setPriority(request.getPriority());
                task.setStartDate(request.getStartDate());
                task.setStatus(request.getStatus() != null ? request.getStatus() : TaskStatus.PENDING);

                // Handle Category Logic
                if (com.pm.farm_backend.enums.Category.Crop_Cycle.equals(request.getCategory())) {
                        if (request.getCropCycleId() == null) {
                                throw new IllegalArgumentException("Crop Cycle ID is required for Crop Cycle tasks.");
                        }
                        com.pm.farm_backend.Model.CropCycle cropCycle = cropCycleRepository
                                        .findById(request.getCropCycleId())
                                        .orElseThrow(() -> new IllegalArgumentException("Crop Cycle not found with id: "
                                                        + request.getCropCycleId()));

                        task.setCropCycle(cropCycle);
                } else {
                        task.setCropCycle(null);
                }

                Task savedTask = taskRepository.save(task);

                // Auto-create expense transaction if expense > 0
                if (savedTask.getExpense() != null && savedTask.getExpense() > 0) {
                        try {
                                com.pm.farm_backend.Dto.farmerDto.FinancialTransactionDTO tx = com.pm.farm_backend.Dto.farmerDto.FinancialTransactionDTO
                                                .builder()
                                                .amount(java.math.BigDecimal.valueOf(savedTask.getExpense()))
                                                .type(com.pm.farm_backend.enums.TransactionType.EXPENSE)
                                                .description("Expense for task: " + savedTask.getName())
                                                .transactionDate(java.time.LocalDate.now())
                                                .category(savedTask.getCategory() != null
                                                                ? savedTask.getCategory().name()
                                                                : "Task Expense")
                                                .userId(user.getId())
                                                .cropCycleId(savedTask.getCropCycle() != null
                                                                ? savedTask.getCropCycle().getId()
                                                                : null)
                                                .build();
                                financeService.addTransaction(user.getEmail(), tx);
                        } catch (Exception e) {
                                System.err.println("Failed to auto-create expense for task: " + e.getMessage());
                                e.printStackTrace();
                        }
                }

                // Create notification for task creation
                try {
                        CreateNotificationDto notificationDto = new CreateNotificationDto();
                        notificationDto.setUserId(user.getId());
                        notificationDto.setType(NotificationType.TASK);
                        notificationDto.setTitle("New Task Created");
                        notificationDto.setMessage("Task '" + savedTask.getName() + "' has been created successfully");
                        notificationDto.setPriority(NotificationPriority.MEDIUM);
                        notificationService.createNotification(notificationDto);
                        logger.info("Notification created for task: {}", savedTask.getName());
                } catch (Exception e) {
                        logger.error("Failed to create notification for task: {}", e.getMessage());
                }

                return savedTask;
        }

        @Override
        public List<TaskResponse> getTasksByFarmerId(String email, String category, String status) {
                FarmerProfile farmer = getFarmerByEmail(email);

                List<Task> tasks;
                if (category != null && !category.isEmpty()) {
                        // Use the Category.fromString method to handle both "General" and "Crop Cycle"
                        Category categoryEnum = Category.fromString(category);
                        tasks = taskRepository.findByFarmerAndCategory(farmer, categoryEnum);
                } else if (status != null && !status.isEmpty()) {
                        TaskStatus statusEnum = TaskStatus.fromString(status);
                        tasks = taskRepository.findByFarmerAndStatus(farmer, statusEnum);
                } else {
                        tasks = taskRepository.findByFarmer(farmer);
                }

                // Map each Task to TaskResponseDTO
                List<TaskResponse> taskDTOs = new ArrayList<>();

                for (Task task : tasks) {
                        TaskResponse dto = new TaskResponse();
                        dto.setId(task.getTaskId());
                        dto.setName(task.getName());
                        dto.setDescription(task.getDescription());
                        dto.setStartDate(task.getStartDate());
                        dto.setDueDate(task.getDueDate());
                        dto.setExpense(task.getExpense());
                        dto.setCategory(task.getCategory() != null ? task.getCategory().getDisplayName() : null);
                        dto.setPriority(task.getPriority() != null ? task.getPriority().getValue() : null);
                        dto.setStatus(task.getStatus() != null ? task.getStatus().getValue() : null);
                        if (task.getCropCycle() != null) {
                                dto.setCropCycleId(task.getCropCycle().getId());
                                dto.setCropCycleName(task.getCropCycle().getCropName());
                        }

                        taskDTOs.add(dto);
                }

                return taskDTOs;
        }

        @Override
        public TaskResponse getTaskById(String email, Long taskId) {
                FarmerProfile farmer = getFarmerByEmail(email);
                Task task = taskRepository
                                .findByTaskIdAndFarmerId(taskId, farmer.getId())
                                .orElseThrow(() -> new RuntimeException("Task not found"));

                TaskResponse dto = new TaskResponse();

                dto.setId(task.getTaskId());
                dto.setName(task.getName());
                dto.setDescription(task.getDescription());
                dto.setStartDate(task.getStartDate());
                dto.setDueDate(task.getDueDate());
                dto.setExpense(task.getExpense());
                dto.setCategory(task.getCategory() != null ? task.getCategory().getDisplayName() : null);
                dto.setPriority(task.getPriority() != null ? task.getPriority().getValue() : null);
                dto.setStatus(task.getStatus() != null ? task.getStatus().getValue() : null);
                if (task.getCropCycle() != null) {
                        dto.setCropCycleId(task.getCropCycle().getId());
                        dto.setCropCycleName(task.getCropCycle().getCropName());
                }

                return dto;

        }

        @Override
        public Task updateTask(
                        String email,
                        Long taskId,
                        CreateTaskRequest dto) {
                FarmerProfile farmer = getFarmerByEmail(email);

                Task task = taskRepository
                                .findByTaskIdAndFarmerId(taskId, farmer.getId())
                                .orElseThrow(() -> new RuntimeException("Task not found"));

                // Update fields
                task.setName(dto.getName());
                task.setDescription(dto.getDescription());
                task.setStartDate(dto.getStartDate());
                task.setDueDate(dto.getDueDate());
                task.setExpense(dto.getExpense());
                task.setPriority(dto.getPriority());

                // Handle Category Change
                task.setCategory(dto.getCategory());
                if (com.pm.farm_backend.enums.Category.Crop_Cycle.equals(dto.getCategory())) {
                        if (dto.getCropCycleId() != null) {
                                com.pm.farm_backend.Model.CropCycle cropCycle = cropCycleRepository
                                                .findById(dto.getCropCycleId())
                                                .orElseThrow(() -> new RuntimeException(
                                                                "Crop Cycle not found with id: "
                                                                                + dto.getCropCycleId()));
                                task.setCropCycle(cropCycle);
                        }
                } else {
                        task.setCropCycle(null);
                }

                return taskRepository.save(task);
        }

        @Override
        public void markTaskAsComplete(String email, Long taskId) {
                FarmerProfile farmer = getFarmerByEmail(email);
                Task task = taskRepository
                                .findByTaskIdAndFarmerId(taskId, farmer.getId())
                                .orElseThrow(() -> new RuntimeException("Task not found"));

                task.setStatus(TaskStatus.COMPLETED);
                taskRepository.save(task);

                // Create notification for task completion
                try {
                        User user = farmer.getUser();
                        CreateNotificationDto notificationDto = new CreateNotificationDto();
                        notificationDto.setUserId(user.getId());
                        notificationDto.setType(NotificationType.TASK);
                        notificationDto.setTitle("Task Completed");
                        notificationDto.setMessage("Congratulations! Task '" + task.getName() + "' has been marked as completed");
                        notificationDto.setPriority(NotificationPriority.HIGH);
                        notificationService.createNotification(notificationDto);
                        logger.info("Notification created for task completion: {}", task.getName());
                } catch (Exception e) {
                        logger.error("Failed to create notification for task completion: {}", e.getMessage());
                }
        }

        @Override
        public void deleteTask(String email, Long taskId) {
                FarmerProfile farmer = getFarmerByEmail(email);
                Task task = taskRepository
                                .findByTaskIdAndFarmerId(taskId, farmer.getId())
                                .orElseThrow(() -> new RuntimeException("Task not found"));

                task.setStatus(TaskStatus.DELETED);
                taskRepository.save(task);
        }

        private FarmerProfile getFarmerByEmail(String email) {
                logger.info("Looking up user by email: {}", email);
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> {
                                        logger.error("User not found: {}", email);
                                        return new IllegalArgumentException("User not found with email: " + email);
                                });

                logger.info("User found: ID={}", user.getId());
                return farmerProfileRepository.findByUser(user)
                                .orElseThrow(() -> {
                                        logger.error("Farmer profile NOT found for user ID: {}", user.getId());
                                        return new IllegalArgumentException(
                                                        "Farmer profile not found for user: " + email);
                                });
        }

}
