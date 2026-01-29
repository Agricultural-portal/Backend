package com.pm.farm_backend.Repositories;

import com.pm.farm_backend.Model.CropCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import com.pm.farm_backend.enums.CropStatus;
import java.util.List;

@Repository
public interface CropCycleRepository extends JpaRepository<CropCycle, Long> {
    List<CropCycle> findByStatusNot(CropStatus status);

    Optional<CropCycle> findByCropNameAndFarmer(String cropName, com.pm.farm_backend.Model.FarmerProfile farmer);

    Integer countByFarmerIdAndStatus(Long farmerId, CropStatus status);

    List<CropCycle> findByFarmerIdAndStatusNot(Long farmerId, CropStatus status);
}
