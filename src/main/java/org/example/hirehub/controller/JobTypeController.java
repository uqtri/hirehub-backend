package org.example.hirehub.controller;

import org.example.hirehub.dto.jobtype.JobTypeDTO;
import org.example.hirehub.dto.jobtype.CreateJobTypeDTO;
import org.example.hirehub.service.JobTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-types")
public class JobTypeController {

    private final JobTypeService jobTypeService;

    public JobTypeController(JobTypeService jobTypeService) {
        this.jobTypeService = jobTypeService;
    }

    @GetMapping("")
    public ResponseEntity<List<JobTypeDTO>> getAllTypes() {
        return ResponseEntity.ok(jobTypeService.getAllActiveTypes());
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<JobTypeDTO> createType(@RequestBody CreateJobTypeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(jobTypeService.createType(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<JobTypeDTO> updateType(
            @PathVariable Long id, 
            @RequestBody CreateJobTypeDTO dto) {
        return ResponseEntity.ok(jobTypeService.updateType(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteType(@PathVariable Long id) {
        jobTypeService.deleteType(id);
        return ResponseEntity.noContent().build();
    }
}

