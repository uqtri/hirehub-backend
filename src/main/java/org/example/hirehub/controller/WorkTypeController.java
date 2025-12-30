package org.example.hirehub.controller;

import org.example.hirehub.dto.worktype.WorkTypeDTO;
import org.example.hirehub.dto.worktype.CreateWorkTypeDTO;
import org.example.hirehub.service.WorkTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-types")
public class WorkTypeController {

    private final WorkTypeService workTypeService;

    public WorkTypeController(WorkTypeService workTypeService) {
        this.workTypeService = workTypeService;
    }

    @GetMapping("")
    public ResponseEntity<List<WorkTypeDTO>> getAllWorkTypes() {
        return ResponseEntity.ok(workTypeService.getAllActiveWorkTypes());
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WorkTypeDTO> createWorkType(@RequestBody CreateWorkTypeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(workTypeService.createWorkType(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WorkTypeDTO> updateWorkType(
            @PathVariable Long id, 
            @RequestBody CreateWorkTypeDTO dto) {
        return ResponseEntity.ok(workTypeService.updateWorkType(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteWorkType(@PathVariable Long id) {
        workTypeService.deleteWorkType(id);
        return ResponseEntity.noContent().build();
    }
}

