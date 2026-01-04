package org.example.hirehub.service;

import org.example.hirehub.dto.worktype.WorkTypeDTO;
import org.example.hirehub.dto.worktype.CreateWorkTypeDTO;
import org.example.hirehub.entity.WorkType;
import org.example.hirehub.repository.WorkTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkTypeService {

    private final WorkTypeRepository workTypeRepository;

    public WorkTypeService(WorkTypeRepository workTypeRepository) {
        this.workTypeRepository = workTypeRepository;
    }

    public List<WorkTypeDTO> getAllActiveWorkTypes() {
        return workTypeRepository.findAllActive().stream()
            .map(type -> new WorkTypeDTO(type.getId(), type.getWorkspace()))
            .collect(Collectors.toList());
    }

    @Transactional
    public WorkTypeDTO createWorkType(CreateWorkTypeDTO dto) {
        if (workTypeRepository.existsByWorkspace(dto.getWorkspace())) {
            throw new RuntimeException("Work type already exists");
        }
        
        WorkType type = new WorkType();
        type.setWorkspace(dto.getWorkspace());
        WorkType saved = workTypeRepository.save(type);
        
        return new WorkTypeDTO(saved.getId(), saved.getWorkspace());
    }

    @Transactional
    public WorkTypeDTO updateWorkType(Long id, CreateWorkTypeDTO dto) {
        WorkType type = workTypeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Work type not found"));
        
        if (!type.getWorkspace().equals(dto.getWorkspace()) && 
            workTypeRepository.existsByWorkspace(dto.getWorkspace())) {
            throw new RuntimeException("Work type already exists");
        }
        
        type.setWorkspace(dto.getWorkspace());
        WorkType updated = workTypeRepository.save(type);
        
        return new WorkTypeDTO(updated.getId(), updated.getWorkspace());
    }

    @Transactional
    public void deleteWorkType(Long id) {
        WorkType type = workTypeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Work type not found"));
        
        type.setDeleted(true);
        workTypeRepository.save(type);
    }
}






