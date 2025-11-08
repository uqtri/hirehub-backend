package org.example.hirehub.service;

import org.example.hirehub.entity.University;
import org.example.hirehub.repository.UniversityRepository;
import org.example.hirehub.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UniversityService {

    private final UniversityRepository universityRepository;

    public UniversityService(UniversityRepository universityRepository) {
        this.universityRepository = universityRepository;
    }

    public University findById(Long id) {
        return universityRepository.findById(id).orElse(null);
    }
    public List<University> findAll() { return universityRepository.findAll();}
}
