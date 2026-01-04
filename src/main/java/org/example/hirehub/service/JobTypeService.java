package org.example.hirehub.service;

import org.example.hirehub.dto.jobtype.JobTypeDTO;
import org.example.hirehub.dto.jobtype.CreateJobTypeDTO;
import org.example.hirehub.entity.JobType;
import org.example.hirehub.repository.JobTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobTypeService {

    private final JobTypeRepository jobTypeRepository;

    public JobTypeService(JobTypeRepository jobTypeRepository) {
        this.jobTypeRepository = jobTypeRepository;
    }

    public List<JobTypeDTO> getAllActiveTypes() {
        return jobTypeRepository.findAllActive().stream()
            .map(type -> new JobTypeDTO(type.getId(), type.getType()))
            .collect(Collectors.toList());
    }

    @Transactional
    public JobTypeDTO createType(CreateJobTypeDTO dto) {
        if (jobTypeRepository.existsByType(dto.getType())) {
            throw new RuntimeException("Job type already exists");
        }
        
        JobType type = new JobType();
        type.setType(dto.getType());
        JobType saved = jobTypeRepository.save(type);
        
        return new JobTypeDTO(saved.getId(), saved.getType());
    }

    @Transactional
    public JobTypeDTO updateType(Long id, CreateJobTypeDTO dto) {
        JobType type = jobTypeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Job type not found"));
        
        if (!type.getType().equals(dto.getType()) && 
            jobTypeRepository.existsByType(dto.getType())) {
            throw new RuntimeException("Job type already exists");
        }
        
        type.setType(dto.getType());
        JobType updated = jobTypeRepository.save(type);
        
        return new JobTypeDTO(updated.getId(), updated.getType());
    }

    @Transactional
    public void deleteType(Long id) {
        JobType type = jobTypeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Job type not found"));
        
        type.setDeleted(true);
        jobTypeRepository.save(type);
    }
}




