package org.example.hirehub.controller;

import org.example.hirehub.dto.joblevel.JobLevelDTO;
import org.example.hirehub.dto.joblevel.CreateJobLevelDTO;
import org.example.hirehub.service.JobLevelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-levels")
public class JobLevelController {

    private final JobLevelService jobLevelService;

    public JobLevelController(JobLevelService jobLevelService) {
        this.jobLevelService = jobLevelService;
    }

    @GetMapping("")
    public ResponseEntity<List<JobLevelDTO>> getAllLevels() {
        return ResponseEntity.ok(jobLevelService.getAllActiveLevels());
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<JobLevelDTO> createLevel(@RequestBody CreateJobLevelDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(jobLevelService.createLevel(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<JobLevelDTO> updateLevel(
            @PathVariable Long id, 
            @RequestBody CreateJobLevelDTO dto) {
        return ResponseEntity.ok(jobLevelService.updateLevel(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteLevel(@PathVariable Long id) {
        jobLevelService.deleteLevel(id);
        return ResponseEntity.noContent().build();
    }
}

