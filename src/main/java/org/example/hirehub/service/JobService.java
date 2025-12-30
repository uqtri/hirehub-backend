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
import org.example.hirehub.dto.notification.CreateNotificationDTO;
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
    private final NotificationService notificationService;
    private final GeminiChatService geminiChatService;

    public JobService(JobRepository jobRepository,
            UserRepository userRepository,
            SkillRepository skillRepository,
            JobMapper jobMapper,
            EmbeddingService embeddingService,
            NotificationService notificationService,
            GeminiChatService geminiChatService) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.jobMapper = jobMapper;
        this.embeddingService = embeddingService;
        this.notificationService = notificationService;
        this.geminiChatService = geminiChatService;
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Page<Job> getAllJobs(String postingDate, String company, String title,
            String location, List<String> levels, List<String> workspaces, List<String> types, List<String> fields,
            String keyword, String province, Long recruiterId, String status, Pageable pageable) {

        // Sanitize Filter Lists: Convert empty lists to null so the repository query
        // ignores them
        if (levels != null && levels.isEmpty())
            levels = null;
        if (workspaces != null && workspaces.isEmpty())
            workspaces = null;
        if (types != null && types.isEmpty())
            types = null;
        if (fields != null && fields.isEmpty())
            fields = null;

        // If recruiterId is provided, filter by recruiter with status and keyword
        if (recruiterId != null) {
            return jobRepository.findByRecruiterWithFilters(recruiterId, status, keyword, pageable);
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
                title, company, location, levels, workspaces, types, fields, dateFilter, keyword, province, pageable);

    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id).orElse(null);
    }

    // Admin method - returns all jobs including banned
    public Page<Job> getAllJobsAdmin(String keyword, String level, String status, String recruiter, Pageable pageable) {
        return jobRepository.searchJobsAdmin(keyword, level, status, recruiter, pageable);
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

        // Check for violations and auto-approve if none found
        try {
            String violationResult = geminiChatService.analyzeJobViolation(
                    savedJob.getTitle(),
                    savedJob.getDescription());

            // Parse JSON result to check if there's a violation
            if (violationResult != null) {
                boolean hasViolation = violationResult.contains("\"hasViolation\": true")
                        || violationResult.contains("\"hasViolation\":true");

                if (!hasViolation) {
                    // No violation - auto approve
                    savedJob.setStatus("APPROVED");
                    savedJob.setIs_banned(false);
                    savedJob.setViolationType(null);
                    savedJob.setViolationExplanation(null);
                } else {
                    // Has violation - save violation info for admin review
                    // Extract violationType
                    String violationType = extractJsonField(violationResult, "violationType");
                    String explanation = extractJsonField(violationResult, "explanation");

                    savedJob.setViolationType(violationType);
                    savedJob.setViolationExplanation(explanation);
                }
                savedJob = jobRepository.save(savedJob);
            }
        } catch (Exception e) {
            // If violation check fails, keep as PENDING for manual review
            // Log the error but don't fail the job creation
        }

        return savedJob;
    }

    @Transactional
    public Job createDraftJob(org.example.hirehub.dto.job.CreateDraftJobRequestDTO request) {
        Job job = new Job();

        // Set only provided fields
        job.setTitle(request.getTitle());
        if (request.getDescription() != null) {
            job.setDescription(request.getDescription());
        }
        if (request.getLevel() != null) {
            job.setLevel(request.getLevel());
        }
        if (request.getWorkspace() != null) {
            job.setWorkspace(request.getWorkspace());
        }
        if (request.getType() != null) {
            job.setType(request.getType());
        }
        if (request.getApplyLink() != null) {
            job.setApply_link(request.getApplyLink());
        }
        if (request.getAddress() != null) {
            job.setAddress(request.getAddress());
        }

        // Set status to DRAFT
        job.setStatus("DRAFT");
        job.setPostingDate(LocalDateTime.now());

        User recruiter = userRepository.findById(request.getRecruiterId())
                .orElseThrow(() -> new UserHandlerException.RecruiterNotFoundException(request.getRecruiterId()));
        job.setRecruiter(recruiter);

        Job insertedJob = jobRepository.save(job);

        // Handle skills if provided
        if (request.getSkillIds() != null && !request.getSkillIds().isEmpty()) {
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

        return insertedJob;
    }

    /**
     * Helper method to extract field value from JSON string
     */
    private String extractJsonField(String json, String fieldName) {
        try {
            String pattern = "\"" + fieldName + "\"\\s*:\\s*\"([^\"]+)\"";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(json);
            if (m.find()) {
                return m.group(1);
            }
        } catch (Exception e) {
            // Ignore parsing errors
        }
        return null;
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
        job.setStatus("CLOSED");

        // Delete embedding when job is soft-deleted
        embeddingService.deleteJobEmbedding(id);

        return jobRepository.save(job);
    }

    @Transactional
    public Job updateJobStatus(Long id, String status) {
        Job job = jobRepository.findById(id).orElseThrow(() -> new JobHandlerException.JobNotFoundException(id));

        if (!status.equals("PENDING") && !status.equals("APPROVED") && !status.equals("BANNED")
                && !status.equals("CLOSED") && !status.equals("DRAFT")) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        job.setStatus(status);

        // If status is CLOSED, also set isDeleted to true
        if (status.equals("CLOSED")) {
            job.setDeleted(true);
        } else {
            job.setDeleted(false);
        }

        // If status is BANNED, also set is_banned to true
        if (status.equals("BANNED")) {
            job.setIs_banned(true);
        } else if (status.equals("APPROVED")) {
            job.setIs_banned(false);
        }

        return jobRepository.save(job);
    }

    @Transactional
    public Job approveJob(Long id, String reason) {
        Job job = jobRepository.findById(id).orElseThrow(() -> new JobHandlerException.JobNotFoundException(id));

        job.setStatus("APPROVED");
        job.setIs_banned(false);

        Job savedJob = jobRepository.save(job);

        // Send notification to recruiter
        User recruiter = job.getRecruiter();
        if (recruiter != null) {
            CreateNotificationDTO notification = CreateNotificationDTO.builder()
                    .userId(recruiter.getId())
                    .type("JOB_UPDATE")
                    .title("Công việc đã được duyệt")
                    .content("Công việc \"" + job.getTitle() + "\" đã được duyệt." +
                            (reason != null && !reason.isEmpty() ? " Lý do: " + reason : ""))
                    .redirectUrl("/recruiter/jobs")
                    .build();
            notificationService.createNotification(notification);
        }

        return savedJob;
    }

    @Transactional
    public Job rejectJob(Long id, String reason) {
        Job job = jobRepository.findById(id).orElseThrow(() -> new JobHandlerException.JobNotFoundException(id));

        job.setStatus("BANNED");
        job.setIs_banned(true);
        job.setBanReason(reason);

        // Delete embedding when job is rejected
        embeddingService.deleteJobEmbedding(id);

        Job savedJob = jobRepository.save(job);

        // Send notification to recruiter
        User recruiter = job.getRecruiter();
        if (recruiter != null) {
            CreateNotificationDTO notification = CreateNotificationDTO.builder()
                    .userId(recruiter.getId())
                    .type("JOB_UPDATE")
                    .title("Công việc bị từ chối")
                    .content("Công việc \"" + job.getTitle() + "\" đã bị từ chối." +
                            (reason != null && !reason.isEmpty() ? " Lý do: " + reason : ""))
                    .redirectUrl("/recruiter/jobs")
                    .build();
            notificationService.createNotification(notification);
        }

        return savedJob;
    }

    @Transactional
    public Job banJob(Long id, String reason) {
        Job job = jobRepository.findById(id).orElseThrow(() -> new JobHandlerException.JobNotFoundException(id));

        job.setStatus("BANNED");
        job.setIs_banned(true);
        job.setBanReason(reason);

        // Delete embedding when job is banned
        embeddingService.deleteJobEmbedding(id);

        Job savedJob = jobRepository.save(job);

        // Send notification to recruiter
        User recruiter = job.getRecruiter();
        if (recruiter != null) {
            CreateNotificationDTO notification = CreateNotificationDTO.builder()
                    .userId(recruiter.getId())
                    .type("JOB_UPDATE")
                    .title("Công việc bị cấm")
                    .content("Công việc \"" + job.getTitle() + "\" đã bị cấm." +
                            (reason != null && !reason.isEmpty() ? " Lý do: " + reason : ""))
                    .redirectUrl("/recruiter/jobs")
                    .build();
            notificationService.createNotification(notification);
        }

        return savedJob;
    }

    @Transactional
    public Job unbanJob(Long id) {
        Job job = jobRepository.findById(id).orElseThrow(() -> new JobHandlerException.JobNotFoundException(id));

        job.setStatus("APPROVED");
        job.setIs_banned(false);

        Job savedJob = jobRepository.save(job);

        // Send notification to recruiter
        User recruiter = job.getRecruiter();
        if (recruiter != null) {
            CreateNotificationDTO notification = CreateNotificationDTO.builder()
                    .userId(recruiter.getId())
                    .type("JOB_UPDATE")
                    .title("Công việc đã được bỏ cấm")
                    .content("Công việc \"" + job.getTitle() + "\" đã được bỏ cấm và hoạt động trở lại.")
                    .redirectUrl("/recruiter/jobs")
                    .build();
            notificationService.createNotification(notification);
        }

        return savedJob;
    }
}
