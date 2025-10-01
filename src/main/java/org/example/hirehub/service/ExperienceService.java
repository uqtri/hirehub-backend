package org.example.hirehub.service;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

import org.example.hirehub.repository.ExperienceRepository;
import org.example.hirehub.entity.Experience;


@Service
public class ExperienceService {
    private final ExperienceRepository experienceRepository;

    ExperienceService(ExperienceRepository experienceRepository) {
        this.experienceRepository = experienceRepository;
    }

    public Experience save(Experience experience) {
        return experienceRepository.save(experience);
    }

    public Optional<Experience> findById(Long id) {
        return experienceRepository.findById(id);
    }

    public List<Experience> getExperiencesByUserId(Long userId) {
        return experienceRepository.getExperienceByUserId(userId);
    }
    public List<Experience> getAllExperiences() {
        return experienceRepository.findAll();
    }
}
