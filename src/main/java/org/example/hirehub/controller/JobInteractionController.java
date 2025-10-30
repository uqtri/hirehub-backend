package org.example.hirehub.controller;

import lombok.Getter;
import org.example.hirehub.dto.jobInteraction.CreateJobInteractionRequestDTO;
import org.example.hirehub.dto.jobInteraction.JobInteractionDetailDTO;
import org.example.hirehub.dto.jobInteraction.JobInteractionFilter;
import org.example.hirehub.entity.JobInteraction;
import org.example.hirehub.mapper.JobInteractionMapper;
import org.example.hirehub.service.JobInteractionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/job-interactions")
public class JobInteractionController {
    private final JobInteractionService jobInteractionService;
    private final JobInteractionMapper jobInteractionMapper;

    public JobInteractionController(JobInteractionService jobInteractionService, JobInteractionMapper jobInteractionMapper) {
        this.jobInteractionService = jobInteractionService;
        this.jobInteractionMapper = jobInteractionMapper;
    }

    @GetMapping("")
    Page<JobInteractionDetailDTO> getJobInteractions(@ModelAttribute JobInteractionFilter filter, Pageable pageable) {


        return jobInteractionService.findAll(filter, pageable).map(jobInteractionMapper::toDTO);
    }
    @PostMapping("")
    ResponseEntity<Map<String, ?>> create(@RequestBody CreateJobInteractionRequestDTO request) {

        JobInteraction jobInteraction = jobInteractionService.create(request);

        if(jobInteraction == null)
            return ResponseEntity.ok().body(Map.of("message", "Xóa thành công"));
        return ResponseEntity.ok().body(Map.of("data", jobInteractionMapper.toDTO(jobInteraction)));
    }
    @DeleteMapping("{id}")
    public ResponseEntity<Map<String, ?>> delete(@PathVariable Long id) {

        jobInteractionService.deleteById(id);

        return ResponseEntity.ok().body(Map.of("message", "Xóa thành công"));
    }
}
