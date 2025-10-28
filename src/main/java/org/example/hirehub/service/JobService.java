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

    public JobService(JobRepository jobRepository,
                      UserRepository userRepository,
                      SkillRepository skillRepository, JobMapper jobMapper){
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.jobMapper = jobMapper;
    }

    public Page<Job> getAllJobs(String postingDate, String company, String title,
                                String location, String level, String workspace,
                                String keyword, String province, Pageable pageable) {

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
                title, company, location, level, workspace, dateFilter, keyword, province, pageable
        );

    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id).orElse(null);
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

        return jobRepository.save(insertedJob);
    }

    @Transactional
    public Job updateJob(UpdateJobRequestDTO request, Long id) {
        Job job = jobRepository.findById(id).orElseThrow(() -> new JobHandlerException.JobNotFoundException(id));

        jobMapper.updateJobFromDTO(job, request);

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

        return jobRepository.save(job);
    }

    @Transactional
    public Job deleteJob(Long id) {
        Job job = jobRepository.findById(id).orElseThrow(()-> new JobHandlerException.JobNotFoundException(id));

        job.setDeleted(true);
        return jobRepository.save(job);
    }
}
