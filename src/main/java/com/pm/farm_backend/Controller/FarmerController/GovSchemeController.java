package com.pm.farm_backend.Controller.FarmerController;

import com.pm.farm_backend.Model.GovScheme;
import com.pm.farm_backend.Service.GovSchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gov-schemes")
@CrossOrigin(origins = "*")
public class GovSchemeController {

    @Autowired
    private GovSchemeService govSchemeService;

    @GetMapping("/all")
    public ResponseEntity<List<GovScheme>> getAllSchemes() {
        return ResponseEntity.ok(govSchemeService.getAllSchemes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GovScheme> getSchemeById(@PathVariable Long id) {
        return govSchemeService.getSchemeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
