package com.pm.farm_backend.Controller.FarmerController;

import com.pm.farm_backend.Model.GovScheme;
import com.pm.farm_backend.Service.FarmerService.GovSchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gov-schemes")
@org.springframework.security.access.prepost.PreAuthorize("hasRole('FARMER')")
public class GovSchemeController {

    @Autowired
    private GovSchemeService govSchemeService;

    @GetMapping("/all")
    public ResponseEntity<List<GovScheme>> getAllSchemes(java.security.Principal principal) {
        return ResponseEntity.ok(govSchemeService.getAllSchemes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GovScheme> getSchemeById(@PathVariable Long id, java.security.Principal principal) {
        return govSchemeService.getSchemeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
