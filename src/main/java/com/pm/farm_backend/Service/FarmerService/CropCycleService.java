package com.pm.farm_backend.Service.FarmerService;

import com.pm.farm_backend.Model.CropCycle;

import java.util.List;
import java.util.Optional;

public interface CropCycleService {

    CropCycle createCropCycle(CropCycle cropCycle, String email);

    List<CropCycle> getAllCropCycles(String email);

    Optional<CropCycle> getCropCycleById(Long id, String email);

    CropCycle updateCropCycle(Long id, CropCycle updatedCropCycle, String email);

    void deleteCropCycle(Long id, String email);
}
