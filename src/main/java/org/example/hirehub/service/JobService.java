package org.example.hirehub.service;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.example.hirehub.exception.SkillHandlerException;
import org.example.hirehub.exception.UserHandlerException;
import org.example.hirehub.exception.JobHandlerException;
import org.example.hirehub.dto.job.CreateJobRequestDTO;
import org.example.hirehub.dto.job.UpdateJobRequestDTO;
import org.example.hirehub.repository.SkillRepository;
import org.example.hirehub.repository.UserRepository;
import org.example.hirehub.repository.JobRepository;
import org.example.hirehub.mapper.JobMapper;
import org.example.hirehub.entity.JobSkill;
import org.example.hirehub.entity.Skill;
import org.example.hirehub.entity.User;
import org.example.hirehub.entity.Job;

@Setter
@Getter
@Service
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final JobMapper jobMapper;
    private final EmbeddingService embeddingService;

    public JobService(JobRepository jobRepository,
            UserRepository userRepository,
            SkillRepository skillRepository,
            JobMapper jobMapper,
            EmbeddingService embeddingService) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.jobMapper = jobMapper;
        this.embeddingService = embeddingService;
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Page<Job> getAllJobs(String postingDate, String company, String title,
            String location, String level, String workspace,
            String keyword, String province, Long recruiterId, Pageable pageable) {

        // If recruiterId is provided, filter by recruiter
        if (recruiterId != null) {
            return jobRepository.findByRecruiterIdAndIsDeletedFalse(recruiterId, pageable);
        }

        LocalDateTime dateFilter = null;
        if (postingDate != null) {
            LocalDateTime now = LocalDateTime.now();
            switch (postingDate) {
                case "24h":
                    dateFilter = now.minusHours(24);
                    break;
                case "1w":
                    dateFilter = now.minusWeeks(1);
                    break;
                case "1m":
                    dateFilter = now.minusMonths(1);
                    break;
            }
        }

        return jobRepository.searchJobsDynamic(
                title, company, location, level, workspace, dateFilter, keyword, province, pageable);

    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id).orElse(null);
    }

    // Admin method - returns all jobs including banned
    public Page<Job> getAllJobsAdmin(String keyword, Pageable pageable) {
        return jobRepository.searchJobsAdmin(keyword, pageable);
    }

    @Transactional
    public Job createJob(CreateJobRequestDTO request) {
        Job job = new Job();

        jobMapper.createJobFromDTO(job, request);

        User recruiter = userRepository.findById(request.getRecruiterId())
                .orElseThrow(() -> new UserHandlerException.RecruiterNotFoundException(request.getRecruiterId()));
        job.setRecruiter(recruiter);

        Job insertedJob = jobRepository.save(job);

        insertedJob.getSkills().clear();
        List<JobSkill> jobSkills = request.getSkillIds().stream()
                .map(skillId -> {
                    Skill skill = skillRepository.findById(skillId)
                            .orElseThrow(() -> new SkillHandlerException.SkillNotFoundException(skillId));
                    return new JobSkill(insertedJob, skill);
                })
                .toList();

        insertedJob.getSkills().addAll(jobSkills);

        Job savedJob = jobRepository.save(insertedJob);

        // Generate embedding asynchronously (pass ID to avoid session issues)
        embeddingService.generateJobEmbeddingAsync(savedJob.getId());

        return savedJob;
    }

    @Transactional
    public Job updateJob(UpdateJobRequestDTO request, Long id) {
        Job job = jobRepository.findById(id).orElseThrow(() -> new JobHandlerException.JobNotFoundException(id));

        jobMapper.updateJobFromDTO(job, request);

        // Explicitly handle is_banned since MapStruct IGNORE strategy may not work for
        // Boolean -> boolean
        if (request.getIs_banned() != null) {
            job.setIs_banned(request.getIs_banned());
        }

        if (request.getSkillIds() != null) {
            job.getSkills().clear();
            List<JobSkill> jobSkills = request.getSkillIds().stream()
                    .map(skillId -> {
                        Skill skill = skillRepository.findById(skillId)
                                .orElseThrow(() -> new SkillHandlerException.SkillNotFoundException(skillId));
                        return new JobSkill(job, skill);
                    })
                    .toList();
            job.getSkills().addAll(jobSkills);
        }

        Job savedJob = jobRepository.save(job);

        // Regenerate embedding asynchronously (pass ID to avoid session issues)
        embeddingService.generateJobEmbeddingAsync(savedJob.getId());

        return savedJob;
    }

    @Transactional
    public Job deleteJob(Long id) {
        Job job = jobRepository.findById(id).orElseThrow(() -> new JobHandlerException.JobNotFoundException(id));

        job.setDeleted(true);

        // Delete embedding when job is soft-deleted
        embeddingService.deleteJobEmbedding(id);

        return jobRepository.save(job);
    }
}
