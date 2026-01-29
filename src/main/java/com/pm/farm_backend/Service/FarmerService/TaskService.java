package com.pm.farm_backend.Service.FarmerService;

import com.pm.farm_backend.Dto.farmerDto.CreateTaskRequest;
import com.pm.farm_backend.Dto.farmerDto.TaskResponse;
import com.pm.farm_backend.Model.Task;

import java.util.List;
import java.util.Map;

public interface TaskService {

    Map<String, Object> getTaskProgressByFarmer(String email);

    Task createTaskForFarmer(CreateTaskRequest request, String email);

    List<TaskResponse> getTasksByFarmerId(String email, String category, String status);

    TaskResponse getTaskById(String email, Long taskId);

    Task updateTask(
            String email,
            Long taskId,
            CreateTaskRequest dto);

    void markTaskAsComplete(String email, Long taskId);

    void deleteTask(String email, Long taskId);

}
