package org.example.hirehub.controller;

import org.example.hirehub.dto.resume.CreateResumeRequestDTO;
import org.example.hirehub.dto.resume.ResumeDetailDTO;
import org.example.hirehub.dto.resume.UpdateResumeRequestDTO;
import org.example.hirehub.entity.Resume;
import org.example.hirehub.mapper.ResumeMapper;
import org.example.hirehub.service.ResumeService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/resumes")

public class ResumeController {

    private final ResumeService resumeService;
    private final ResumeMapper resumeMapper;

    public ResumeController(ResumeService resumeService, ResumeMapper resumeMapper) {
        this.resumeService = resumeService;
        this.resumeMapper = resumeMapper;
    }

    @GetMapping("")
    public List<ResumeDetailDTO> getAllResumes(
            @RequestParam(required = false) Long user,
            @RequestParam(required = false) Long job,
            @RequestParam(required = false) Long recruiter,
            @RequestParam(required = false) String status) {
        return resumeService.getAllResumes(user, job, recruiter, status).stream().map(resumeMapper::toDTO).toList();
    }

    @GetMapping("/{id}")
    public ResumeDetailDTO getResumeById(@PathVariable Long id) {
        return resumeMapper.toDTO(resumeService.getResumeById(id));
    }

    @PostMapping("")
    public ResponseEntity<ResumeDetailDTO> createResume(
            @Valid @ModelAttribute CreateResumeRequestDTO request) throws IOException {
        ResumeDetailDTO resume = resumeMapper.toDTO(resumeService.createResume(request));
        return ResponseEntity.ok(resume);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResumeDetailDTO> updateResume(
            @PathVariable Long id,
            @Valid @RequestBody UpdateResumeRequestDTO request) {
        Resume updatedResume = resumeService.updateResume(request, id);
        return ResponseEntity.ok(resumeMapper.toDTO(updatedResume));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResumeDetailDTO> deleteResume(@PathVariable Long id) {
        Resume deletedResume = resumeService.deleteResume(id);
        return ResponseEntity.ok(resumeMapper.toDTO(deletedResume));
    }

}
