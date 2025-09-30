package org.example.hirehub.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hirehub.dto.job.CreateJobRequestDTO;
import org.example.hirehub.dto.job.UpdateJobRequestDTO;
import org.example.hirehub.entity.*;
import org.example.hirehub.mapper.JobMapper;
import org.example.hirehub.mapper.ResumeMapper;
import org.example.hirehub.repository.ResumeRepository;
import org.example.hirehub.repository.SkillRepository;
import org.example.hirehub.repository.UserRepository;
import org.springframework.stereotype.Service;

import org.example.hirehub.repository.JobRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final ResumeMapper resumeMapper;

    public ResumeService(ResumeRepository resumeRepository, ResumeMapper resumeMapper){
        this.resumeRepository = resumeRepository;
        this.resumeMapper = resumeMapper;
    }

    public List<Resume> getAllResumes() {

       return resumeRepository.findAll();

    }

    public Resume getResumeById(Long id) {
        return resumeRepository.findById(id).orElse(null);
    }

    public List<Resume> getResumeForCompany(Long id) {
        return resumeRepository.findResumesForCompany(id);
    }

    public List<Resume> getResumeForUser(Long id) {
        return resumeRepository.findResumesForUser(id);
    }

//    @Transactional
//    public Job createJob(CreateJobRequestDTO request) {
//        Job job = new Job();
//
//        jobMapper.createJobFromDTO(job, request);
//
//        User recruiter = userRepository.findById(request.getRecruiterId())
//                .orElseThrow(() -> new RuntimeException("Recruiter not found"));
//        job.setRecruiter(recruiter);
//
//        Job insertedJob = jobRepository.save(job);
//
//        insertedJob.getSkills().clear();
//        List<JobSkill> jobSkills = request.getSkillIds().stream()
//                .map(skillId -> {
//                    Skill skill = skillRepository.findById(skillId)
//                            .orElseThrow(() -> new RuntimeException("Skill not found with id: " + skillId));
//                    return new JobSkill(insertedJob, skill);
//                })
//                .toList();
//
//        insertedJob.getSkills().addAll(jobSkills);
//
//        return jobRepository.save(insertedJob);
//    }
//
//    @Transactional
//    public Job updateJob(UpdateJobRequestDTO request, Long id) {
//        Job job = jobRepository.findById(id).orElse(null);
//
//        if (job == null) {
//            return null;
//        }
//
//        jobMapper.updateJobFromDTO(job, request);
//
//        if (request.getSkillIds() != null) {
//            job.getSkills().clear();
//            List<JobSkill> jobSkills = request.getSkillIds().stream()
//                    .map(skillId -> {
//                        Skill skill = skillRepository.findById(skillId)
//                                .orElseThrow(() -> new RuntimeException("Skill not found with id: " + skillId));
//                        return new JobSkill(job, skill);
//                    })
//                    .toList();
//            job.getSkills().addAll(jobSkills);
//        }
//
//        return jobRepository.save(job);
//    }
//
//    @Transactional
//    public Job deleteJob(Long id) {
//        Job job = jobRepository.findById(id).orElse(null);
//        if(job == null) {
//            return null;
//        }
//
//        job.setDeleted(true);
//        return jobRepository.save(job);
//    }
}
