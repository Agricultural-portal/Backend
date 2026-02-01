package com.pm.farm_backend.Service.FarmerService;

import com.pm.farm_backend.Dto.CreateNotificationDto;
import com.pm.farm_backend.Model.CropCycle;
import com.pm.farm_backend.Repositories.CropCycleRepository;
import com.pm.farm_backend.Service.NotificationService;
import com.pm.farm_backend.enums.NotificationPriority;
import com.pm.farm_backend.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CropCycleServiceImpl implements CropCycleService {

    private final CropCycleRepository cropCycleRepository;
    private final com.pm.farm_backend.Repositories.FarmerProfileRepository farmerProfileRepository;
    private final com.pm.farm_backend.Repositories.UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    public CropCycle createCropCycle(CropCycle cropCycle, String email) {
        com.pm.farm_backend.Model.FarmerProfile farmer = getFarmerByEmail(email);
        cropCycle.setFarmer(farmer);
        checkAndUpdateStatus(cropCycle);
        CropCycle saved = cropCycleRepository.save(cropCycle);

        // Create notification for crop cycle creation
        try {
            com.pm.farm_backend.Model.User user = farmer.getUser();
            CreateNotificationDto notificationDto = new CreateNotificationDto();
            notificationDto.setUserId(user.getId());
            notificationDto.setType(NotificationType.TASK);
            notificationDto.setTitle("New Crop Cycle Started");
            notificationDto.setMessage("Crop cycle for '" + saved.getCropName() + "' has been started successfully");
            notificationDto.setPriority(NotificationPriority.HIGH);
            notificationService.createNotification(notificationDto);
            log.info("Notification created for crop cycle: {}", saved.getCropName());
        } catch (Exception e) {
            log.error("Failed to create notification for crop cycle: {}", e.getMessage());
        }

        return saved;
    }

    @Override
    public List<CropCycle> getAllCropCycles(String email) {
        com.pm.farm_backend.Model.FarmerProfile farmer = getFarmerByEmail(email);
        List<CropCycle> cycles = cropCycleRepository.findByFarmerIdAndStatusNot(farmer.getId(),
                com.pm.farm_backend.enums.CropStatus.DELETED);
        cycles.forEach(this::checkAndUpdateStatus);
        return cycles;
    }

    @Override
    public Optional<CropCycle> getCropCycleById(Long id, String email) {
        com.pm.farm_backend.Model.FarmerProfile farmer = getFarmerByEmail(email);
        Optional<CropCycle> cropCycle = cropCycleRepository.findById(id);

        if (cropCycle.isPresent()) {
            if (!cropCycle.get().getFarmer().getId().equals(farmer.getId())) {
                throw new RuntimeException("Unauthorized: This crop cycle does not belong to you.");
            }
            checkAndUpdateStatus(cropCycle.get());
        }
        return cropCycle;
    }

    @Override
    public CropCycle updateCropCycle(Long id, CropCycle updatedCropCycle, String email) {
        com.pm.farm_backend.Model.FarmerProfile farmer = getFarmerByEmail(email);

        return cropCycleRepository.findById(id).map(existing -> {
            if (!existing.getFarmer().getId().equals(farmer.getId())) {
                throw new RuntimeException("Unauthorized: You can only update your own crop cycles.");
            }

            existing.setCropName(updatedCropCycle.getCropName());
            existing.setVariety(updatedCropCycle.getVariety());
            existing.setSowingDate(updatedCropCycle.getSowingDate());
            existing.setExpectedHarvestDate(updatedCropCycle.getExpectedHarvestDate());
            existing.setTotalArea(updatedCropCycle.getTotalArea());

            checkAndUpdateStatus(existing);
            return cropCycleRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("CropCycle not found with id " + id));
    }

    @Override
    public void deleteCropCycle(Long id, String email) {
        com.pm.farm_backend.Model.FarmerProfile farmer = getFarmerByEmail(email);

        CropCycle cropCycle = cropCycleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CropCycle not found with id " + id));

        if (!cropCycle.getFarmer().getId().equals(farmer.getId())) {
            throw new RuntimeException("Unauthorized: You can only delete your own crop cycles.");
        }

        cropCycle.setStatus(com.pm.farm_backend.enums.CropStatus.DELETED);
        cropCycleRepository.save(cropCycle);
    }

    private com.pm.farm_backend.Model.FarmerProfile getFarmerByEmail(String email) {
        com.pm.farm_backend.Model.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return farmerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Farmer profile not found for user: " + email));
    }

    private void checkAndUpdateStatus(CropCycle cropCycle) {
        if (cropCycle.getStatus() == com.pm.farm_backend.enums.CropStatus.DELETED) {
            return;
        }

        java.time.LocalDate now = java.time.LocalDate.now();
        com.pm.farm_backend.enums.CropStatus newStatus = cropCycle.getStatus();

        if (cropCycle.getSowingDate() != null && now.isBefore(cropCycle.getSowingDate())) {
            newStatus = com.pm.farm_backend.enums.CropStatus.PENDING;
        } else if (cropCycle.getSowingDate() != null && cropCycle.getExpectedHarvestDate() != null) {
            if (!now.isBefore(cropCycle.getSowingDate()) && now.isBefore(cropCycle.getExpectedHarvestDate())) {
                newStatus = com.pm.farm_backend.enums.CropStatus.IN_PROGRESS;
            } else if (!now.isBefore(cropCycle.getExpectedHarvestDate())) {
                newStatus = com.pm.farm_backend.enums.CropStatus.COMPLETED;
            }
        }

        if (newStatus != cropCycle.getStatus()) {
            cropCycle.setStatus(newStatus);
            cropCycleRepository.save(cropCycle);
        }
    }
}
