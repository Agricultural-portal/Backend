package com.pm.farm_backend.Repositories;

import com.pm.farm_backend.Model.FarmerProfile;
import com.pm.farm_backend.Model.Task;
import com.pm.farm_backend.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    long countByFarmer_Id(Long farmerId);

    long countByFarmer_IdAndStatus(Long farmerId, TaskStatus status);

    List<Task> findByFarmer(FarmerProfile farmer);

    Optional<Task> findByTaskIdAndFarmerId(Long taskId, Long farmerId);

    List<Task> findByFarmerAndCategory(FarmerProfile farmer, com.pm.farm_backend.enums.Category category);

    List<Task> findByFarmerAndStatus(FarmerProfile farmer, TaskStatus status);
}
