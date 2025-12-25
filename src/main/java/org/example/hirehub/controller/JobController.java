package org.example.hirehub.controller;

import org.example.hirehub.dto.job.UpdateJobRequestDTO;
import org.example.hirehub.repository.ResumeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.example.hirehub.entity.Job;
import jakarta.validation.Valid;

import org.example.hirehub.dto.job.CreateJobRequestDTO;
import org.example.hirehub.dto.job.JobDetailDTO;
import org.example.hirehub.service.JobService;
import org.example.hirehub.mapper.JobMapper;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")

public class JobController {

    private final JobService jobService;
    private final JobMapper jobMapper;
    private final ResumeRepository resumeRepository;

    public JobController(JobService jobService, JobMapper jobMapper, ResumeRepository resumeRepository) {
        this.jobService = jobService;
        this.jobMapper = jobMapper;
        this.resumeRepository = resumeRepository;
    }

    @GetMapping("")
    public Page<JobDetailDTO> getAllJobs(
            @RequestParam(required = false) String postingDate,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String workspace,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) Long recruiterId,
            @RequestParam(required = false) String status,
            @PageableDefault(page = 0, size = 10, sort = "postingDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Job> jobs = jobService.getAllJobs(postingDate, company, title, location, level, workspace, keyword,
                province, recruiterId, status, pageable);
        return jobs.map(job -> {
            JobDetailDTO dto = jobMapper.toDTO(job);
            dto.setCandidatesCount(resumeRepository.countByJobId(job.getId()));
            return dto;
        });
    }

    // Admin endpoint - shows all jobs including banned ones
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<JobDetailDTO> getAllJobsAdmin(
            @RequestParam(required = false) String keyword,
            @PageableDefault(page = 0, size = 10, sort = "postingDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Job> jobs = jobService.getAllJobsAdmin(keyword, pageable);
        return jobs.map(job -> {
            JobDetailDTO dto = jobMapper.toDTO(job);
            dto.setCandidatesCount(resumeRepository.countByJobId(job.getId()));
            return dto;
        });
    }

    @GetMapping("/{id}")
    public JobDetailDTO getById(@PathVariable Long id) {
        return jobMapper.toDTO(jobService.getJobById(id));
    }

    @PreAuthorize("hasRole('RECRUITER') and hasAuthority('CREATE_JOB')")
    @PostMapping("")
    public ResponseEntity<JobDetailDTO> createJob(
            @Valid @RequestBody CreateJobRequestDTO request) {
        JobDetailDTO job = jobMapper.toDTO(jobService.createJob(request));
        return ResponseEntity.ok(job);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobDetailDTO> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody UpdateJobRequestDTO request) {
        Job updatedJob = jobService.updateJob(request, id);
        return ResponseEntity.ok(jobMapper.toDTO(updatedJob));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<JobDetailDTO> deleteJob(@PathVariable Long id) {
        Job deletedJob = jobService.deleteJob(id);
        return ResponseEntity.ok(jobMapper.toDTO(deletedJob));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<JobDetailDTO> updateJobStatus(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, String> request) {
        String status = request.get("status");
        Job updatedJob = jobService.updateJobStatus(id, status);
        return ResponseEntity.ok(jobMapper.toDTO(updatedJob));
    }

}
