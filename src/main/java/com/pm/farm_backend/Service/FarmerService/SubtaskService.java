package com.pm.farm_backend.Service.FarmerService;

import com.pm.farm_backend.Model.Subtask;
import com.pm.farm_backend.enums.TaskStatus;
import java.util.List;

public interface SubtaskService {
    Subtask createSubtask(String email, Long cropCycleId, Subtask subtask);

    List<Subtask> getSubtasksByCropId(String email, Long cropCycleId);

    Subtask updateSubtask(String email, Long id, Subtask subtaskDetails);

    Subtask updateSubtaskStatus(String email, Long id, TaskStatus status);

    void deleteSubtask(String email, Long id);
}
