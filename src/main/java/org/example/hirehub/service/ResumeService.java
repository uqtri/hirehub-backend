package org.example.hirehub.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.example.hirehub.dto.resume.CreateResumeRequestDTO;
import org.example.hirehub.dto.resume.UpdateResumeRequestDTO;
import org.example.hirehub.repository.ResumeRepository;
import org.example.hirehub.repository.UserRepository;
import org.example.hirehub.repository.JobRepository;
import org.example.hirehub.exception.ResumeHandlerException;
import org.example.hirehub.exception.JobHandlerException;
import org.example.hirehub.mapper.ResumeMapper;
import org.example.hirehub.entity.*;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final ResumeMapper resumeMapper;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final CloudinaryService cloudinaryService;
    private final OpenAiFileService openAiFileService;

    public ResumeService(ResumeRepository resumeRepository, ResumeMapper resumeMapper,
                         UserRepository userRepository,
                         JobRepository jobRepository, CloudinaryService cloudinaryService, OpenAiFileService openAiFileService){
        this.resumeRepository = resumeRepository;
        this.resumeMapper = resumeMapper;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.cloudinaryService = cloudinaryService;
        this.openAiFileService = openAiFileService;
    }

    public List<Resume> getAllResumes(Long user, Long job, Long recruiter) {
       return resumeRepository.searchResumesDynamic(user, job, recruiter);
    }

    public Resume getResumeById(Long id) {
        return resumeRepository.findById(id).orElseThrow(() -> new ResumeHandlerException.ResumeNotFoundException(id));
    }

    @Transactional
    public Resume createResume(CreateResumeRequestDTO request) throws IOException {
        Resume resume = new Resume();

        List<Resume> existingResume = resumeRepository.searchResumesDynamic(request.getUserId(), request.getJobId(), null);

        if(!existingResume.isEmpty()) {
            throw new ResumeHandlerException.ResumeAlreadyApplied(request.getJobId());

        }
        resumeMapper.createResumeFromDTO(resume, request);

        MultipartFile resumeFile = request.getResumeFile();
        if(resumeFile != null && !resumeFile.isEmpty()) {
            String url = cloudinaryService.uploadAndGetUrl(resumeFile, Map.of());
//            String openAiResumeId = openAiFileService.uploadFile(resumeFile);
            resume.setLink(url);
//            resume.setOpenAiResumeId(openAiResumeId);
        }
        else {
            resume.setLink(request.getLink());
            resume.setOpenAiResumeId(request.getOpenAiResumeId());
        }

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
