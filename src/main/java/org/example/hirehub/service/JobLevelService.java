package org.example.hirehub.service;

import org.example.hirehub.dto.joblevel.JobLevelDTO;
import org.example.hirehub.dto.joblevel.CreateJobLevelDTO;
import org.example.hirehub.entity.JobLevel;
import org.example.hirehub.repository.JobLevelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobLevelService {

    private final JobLevelRepository jobLevelRepository;

    public JobLevelService(JobLevelRepository jobLevelRepository) {
        this.jobLevelRepository = jobLevelRepository;
    }

    public List<JobLevelDTO> getAllActiveLevels() {
        return jobLevelRepository.findAllActive().stream()
            .map(level -> new JobLevelDTO(level.getId(), level.getLevel()))
            .collect(Collectors.toList());
    }

    @Transactional
    public JobLevelDTO createLevel(CreateJobLevelDTO dto) {
        if (jobLevelRepository.existsByLevel(dto.getLevel())) {
            throw new RuntimeException("Job level already exists");
        }
        
        JobLevel level = new JobLevel();
        level.setLevel(dto.getLevel());
        JobLevel saved = jobLevelRepository.save(level);
        
        return new JobLevelDTO(saved.getId(), saved.getLevel());
    }

    @Transactional
    public JobLevelDTO updateLevel(Long id, CreateJobLevelDTO dto) {
        JobLevel level = jobLevelRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Job level not found"));
        
        if (!level.getLevel().equals(dto.getLevel()) && 
            jobLevelRepository.existsByLevel(dto.getLevel())) {
            throw new RuntimeException("Job level already exists");
        }
        
        level.setLevel(dto.getLevel());
        JobLevel updated = jobLevelRepository.save(level);
        
        return new JobLevelDTO(updated.getId(), updated.getLevel());
    }

    @Transactional
    public void deleteLevel(Long id) {
        JobLevel level = jobLevelRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Job level not found"));
        
        level.setDeleted(true);
        jobLevelRepository.save(level);
    }
}





