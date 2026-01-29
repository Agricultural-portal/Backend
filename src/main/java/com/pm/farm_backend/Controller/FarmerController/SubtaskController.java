package com.pm.farm_backend.Controller.FarmerController;

import com.pm.farm_backend.Model.Subtask;
import com.pm.farm_backend.Service.FarmerService.SubtaskService;
import com.pm.farm_backend.enums.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subtasks")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FARMER')")
public class SubtaskController {

    private final SubtaskService subtaskService;

    @PostMapping("/crop-cycle/{cropCycleId}")
    public ResponseEntity<?> createSubtask(@PathVariable Long cropCycleId, @RequestBody Subtask subtask,
            java.security.Principal principal) {
        try {
            return ResponseEntity.ok(subtaskService.createSubtask(principal.getName(), cropCycleId, subtask));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/crop-cycle/{cropCycleId}")
    public ResponseEntity<List<Subtask>> getSubtasksByCropId(@PathVariable Long cropCycleId,
            java.security.Principal principal) {
        return ResponseEntity.ok(subtaskService.getSubtasksByCropId(principal.getName(), cropCycleId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSubtask(@PathVariable Long id, @RequestBody Subtask subtask,
            java.security.Principal principal) {
        try {
            return ResponseEntity.ok(subtaskService.updateSubtask(principal.getName(), id, subtask));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Subtask> updateSubtaskStatus(@PathVariable Long id, @RequestParam TaskStatus status,
            java.security.Principal principal) {
        try {
            return ResponseEntity.ok(subtaskService.updateSubtaskStatus(principal.getName(), id, status));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubtask(@PathVariable Long id, java.security.Principal principal) {
        subtaskService.deleteSubtask(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
