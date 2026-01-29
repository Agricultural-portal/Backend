package com.pm.farm_backend.Controller.FarmerController;

import com.pm.farm_backend.Model.CropCycle;
import com.pm.farm_backend.Service.FarmerService.CropCycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crop-cycles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FARMER')")
public class CropCycleController {

    private final CropCycleService cropCycleService;

    @PostMapping
    public ResponseEntity<CropCycle> createCropCycle(org.springframework.security.core.Authentication authentication,
            @RequestBody CropCycle cropCycle) {
        String email = authentication.getName();
        return ResponseEntity.ok(cropCycleService.createCropCycle(cropCycle, email));
    }

    @GetMapping
    public ResponseEntity<List<CropCycle>> getAllCropCycles(
            org.springframework.security.core.Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(cropCycleService.getAllCropCycles(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CropCycle> getCropCycleById(org.springframework.security.core.Authentication authentication,
            @PathVariable Long id) {
        String email = authentication.getName();
        return cropCycleService.getCropCycleById(id, email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CropCycle> updateCropCycle(org.springframework.security.core.Authentication authentication,
            @PathVariable Long id, @RequestBody CropCycle cropCycle) {
        String email = authentication.getName();
        try {
            return ResponseEntity.ok(cropCycleService.updateCropCycle(id, cropCycle, email));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCropCycle(org.springframework.security.core.Authentication authentication,
            @PathVariable Long id) {
        String email = authentication.getName();
        cropCycleService.deleteCropCycle(id, email);
        return ResponseEntity.noContent().build();
    }
}
