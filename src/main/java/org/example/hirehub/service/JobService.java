package org.example.hirehub.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hirehub.dto.job.CreateJobRequestDTO;
import org.example.hirehub.entity.JobSkill;
import org.example.hirehub.entity.Skill;
import org.example.hirehub.entity.User;
import org.example.hirehub.repository.SkillRepository;
import org.example.hirehub.repository.UserRepository;
import org.springframework.stereotype.Service;

import org.example.hirehub.repository.JobRepository;
import org.example.hirehub.entity.Job;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Service
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public JobService(JobRepository jobRepository,
                      UserRepository userRepository,
                      SkillRepository skillRepository){
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
    }

    public List<Job> getAllJobs(String postingDate, String company, String title,
                               String location, String level, String workspace,
                               String keyword) {

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

        List<Job> jobs = jobRepository.searchJobsDynamic(
                title, company, location, level, workspace, dateFilter,keyword
        );

        return jobs;
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id).orElse(null);
    }

    public Job createJob(CreateJobRequestDTO request) {
        Job job = new Job();
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setApply_link(request.getApplyLink());
        job.setLevel(request.getLevel());
        job.setWorkspace(request.getWorkspace());

        // recruiter
        User recruiter = userRepository.findById(request.getRecruiterId())
                .orElseThrow(() -> new RuntimeException("Recruiter not found"));
        job.setRecruiter(recruiter);

        // skills
        List<JobSkill> jobSkills = request.getSkillIds().stream()
                .map(skillId -> {
                    Skill skill = skillRepository.findById(skillId)
                            .orElseThrow(() -> new RuntimeException("Skill not found"));
                    return new JobSkill(job, skill);
                })
                .toList();

        job.getSkills().addAll(jobSkills);

        return jobRepository.save(job);
    }

    public Job updateJob(Job job) {
        return jobRepository.save(job);
    }

    public void deleteJob(Long id) {
        Job job = jobRepository.findById(id).orElse(null);
        if(job == null) {
            return;
        }

        job.setDeleted(true);
        jobRepository.save(job);
    }
}
