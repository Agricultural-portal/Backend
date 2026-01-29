package com.pm.farm_backend.Repositories;

import com.pm.farm_backend.Model.Subtask;
import com.pm.farm_backend.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubtaskRepository extends JpaRepository<Subtask, Long> {
    List<Subtask> findByCropCycleIdAndStatusNot(Long cropCycleId, TaskStatus status);

    Integer countByCropCycleFarmerId(Long farmerId);

    Integer countByCropCycleFarmerIdAndStatus(Long farmerId, TaskStatus status);

    org.springframework.data.domain.Page<Subtask> findByCropCycleFarmerIdOrderByDueDateAsc(Long farmerId,
            org.springframework.data.domain.Pageable pageable);
}
