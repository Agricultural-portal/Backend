package com.pm.farm_backend.Controller.FarmerController;

import com.pm.farm_backend.Dto.farmerDto.CreateTaskRequest;

import com.pm.farm_backend.Dto.farmerDto.TaskResponse;
import com.pm.farm_backend.Model.Task;
import com.pm.farm_backend.Service.FarmerService.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/task")
@PreAuthorize("hasRole('FARMER')")
public class TaskController {
    @Autowired
    private TaskService taskService;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TaskController.class);

    @GetMapping("/progress")
    public ResponseEntity<?> getProgressByFarmer(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(
                taskService.getTaskProgressByFarmer(email));
    }

    @PostMapping("/create")
    public ResponseEntity<Task> createTask(
            Authentication authentication,
            @RequestBody CreateTaskRequest request) {

        String email = authentication.getName();
        logger.info("Received create task request from: {}", email);
        logger.info("Payload: Name={}, Category={}, Priority={}",
                request.getName(), request.getCategory(), request.getPriority());

        Task task = taskService.createTaskForFarmer(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @GetMapping("/alltask")
    public ResponseEntity<List<TaskResponse>> getTasksByFarmer(
            Authentication authentication,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status) {

        String email = authentication.getName();
        List<TaskResponse> tasks = taskService.getTasksByFarmerId(email, category, status);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(
            Authentication authentication,
            @PathVariable Long taskId) {

        String email = authentication.getName();
        TaskResponse task = taskService.getTaskById(email, taskId);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTask(
            Authentication authentication,
            @PathVariable Long taskId,
            @RequestBody CreateTaskRequest dto) {

        String email = authentication.getName();
        Task updatedTask = taskService.updateTask(email, taskId, dto);

        return ResponseEntity.ok(updatedTask);
    }

    @PatchMapping("/{taskId}/complete")
    public ResponseEntity<Void> markTaskAsComplete(
            Authentication authentication,
            @PathVariable Long taskId) {
        String email = authentication.getName();
        taskService.markTaskAsComplete(email, taskId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            Authentication authentication,
            @PathVariable Long taskId) {
        String email = authentication.getName();
        taskService.deleteTask(email, taskId);
        return ResponseEntity.noContent().build();
    }
}
