package org.example.hirehub.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;

import org.springframework.stereotype.Service;

import java.util.List;

import org.example.hirehub.dto.resume.CreateResumeRequestDTO;
import org.example.hirehub.dto.resume.UpdateResumeRequestDTO;
import org.example.hirehub.repository.ResumeRepository;
import org.example.hirehub.repository.UserRepository;
import org.example.hirehub.repository.JobRepository;
import org.example.hirehub.exception.ResumeHandlerException;
import org.example.hirehub.exception.JobHandlerException;
import org.example.hirehub.mapper.ResumeMapper;
import org.example.hirehub.entity.*;

@Setter
@Getter
@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final ResumeMapper resumeMapper;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public ResumeService(ResumeRepository resumeRepository, ResumeMapper resumeMapper,
                         UserRepository userRepository,
                         JobRepository jobRepository){
        this.resumeRepository = resumeRepository;
        this.resumeMapper = resumeMapper;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    public List<Resume> getAllResumes(Long user, Long job, Long recruiter) {
       return resumeRepository.searchResumesDynamic(user, job, recruiter);
    }

    public Resume getResumeById(Long id) {
        return resumeRepository.findById(id).orElseThrow(() -> new ResumeHandlerException.ResumeNotFoundException(id));
    }

    @Transactional
    public Resume createResume(CreateResumeRequestDTO request) {
        Resume resume = new Resume();

        resumeMapper.createResumeFromDTO(resume, request);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Job job = jobRepository.findById(request.getJobId()).orElseThrow(() -> new JobHandlerException.JobNotFoundException(request.getJobId()));


        resume.setUser(user);
        resume.setJob(job);

        return resumeRepository.save(resume);
    }

    @Transactional
    public Resume updateResume(UpdateResumeRequestDTO request, Long id) {
        Resume resume = resumeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Resume not found with id " + id));

        resumeMapper.updateResumeFromDTO(resume, request);

        return resumeRepository.save(resume);
    }

    @Transactional
    public Resume deleteResume(Long id) {
        Resume resume = resumeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Resume not found with id " + id));

        resume.setDeleted(true);
        return resumeRepository.save(resume);
    }
}
