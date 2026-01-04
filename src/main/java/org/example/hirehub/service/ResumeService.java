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
import org.example.hirehub.dto.notification.CreateNotificationDTO;
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
    private final NotificationService notificationService;

    public ResumeService(ResumeRepository resumeRepository, ResumeMapper resumeMapper,
            UserRepository userRepository,
            JobRepository jobRepository, CloudinaryService cloudinaryService, OpenAiFileService openAiFileService,
            NotificationService notificationService) {
        this.resumeRepository = resumeRepository;
        this.resumeMapper = resumeMapper;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.cloudinaryService = cloudinaryService;
        this.openAiFileService = openAiFileService;
        this.notificationService = notificationService;
    }

    public List<Resume> getAllResumes(Long user, Long job, Long recruiter, String status) {
        return resumeRepository.searchResumesDynamic(user, job, recruiter, status);
    }

    public Resume getResumeById(Long id) {
        return resumeRepository.findById(id).orElseThrow(() -> new ResumeHandlerException.ResumeNotFoundException(id));
    }

    @Transactional
    public Resume createResume(CreateResumeRequestDTO request) throws IOException {
        Resume resume = new Resume();

        List<Resume> existingResume = resumeRepository.searchResumesDynamic(request.getUserId(), request.getJobId(),
                null, null);

        if (!existingResume.isEmpty()) {
            throw new ResumeHandlerException.ResumeAlreadyApplied(request.getJobId());

        }
        resumeMapper.createResumeFromDTO(resume, request);

        MultipartFile resumeFile = request.getResumeFile();
        if (resumeFile != null && !resumeFile.isEmpty()) {
            String url = cloudinaryService.uploadAndGetUrl(resumeFile, Map.of("async", true));
            // String openAiResumeId = openAiFileService.uploadFile(resumeFile);
            resume.setLink(url);
            // resume.setOpenAiResumeId(openAiResumeId);
        } else {
            resume.setLink(request.getLink());
            resume.setOpenAiResumeId(request.getOpenAiResumeId());
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new JobHandlerException.JobNotFoundException(request.getJobId()));

        resume.setUser(user);
        resume.setJob(job);

        return resumeRepository.save(resume);
    }

    @Transactional
    public Resume updateResume(UpdateResumeRequestDTO request, Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resume not found with id " + id));

        String oldStatus = resume.getStatus();
        resumeMapper.updateResumeFromDTO(resume, request);
        String newStatus = request.getStatus();

        Resume savedResume = resumeRepository.save(resume);

        // Send notification if status changed to ACCEPTED or REJECTED
        if (newStatus != null && !newStatus.equals(oldStatus)) {
            if ("ACCEPTED".equals(newStatus) || "REJECTED".equals(newStatus)) {
                sendStatusNotification(savedResume, newStatus);
            }
        }

        return savedResume;
    }

    private void sendStatusNotification(Resume resume, String status) {
        User applicant = resume.getUser();
        Job job = resume.getJob();
        String recruiterName = job.getRecruiter().getName();

        String title;
        String content;

        if ("ACCEPTED".equals(status)) {
            title = "🎉 Đơn ứng tuyển được chấp nhận!";
            content = String.format(
                    "Chúc mừng! %s đã chấp nhận đơn ứng tuyển của bạn cho vị trí \"%s\". Hãy chờ nhà tuyển dụng liên hệ với bạn nhé!",
                    recruiterName, job.getTitle());
        } else {
            title = "📋 Cập nhật đơn ứng tuyển";
            content = String.format(
                    "Rất tiếc, %s đã từ chối đơn ứng tuyển của bạn cho vị trí \"%s\". Đừng nản lòng, hãy tiếp tục tìm kiếm cơ hội phù hợp!",
                    recruiterName, job.getTitle());
        }

        CreateNotificationDTO notificationDTO = new CreateNotificationDTO();
        notificationDTO.setUserId(applicant.getId());
        notificationDTO.setType("APPLICATION_STATUS");
        notificationDTO.setTitle(title);
        notificationDTO.setContent(content);
        notificationDTO.setRedirectUrl("/my-jobs");

        notificationService.createNotification(notificationDTO);
    }

    @Transactional
    public Resume deleteResume(Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resume not found with id " + id));

        resume.setDeleted(true);
        return resumeRepository.save(resume);
    }

    @Transactional
    public Resume banResume(Long id, String reason) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resume not found with id " + id));

        // Store status before banning
        resume.setPreviousStatus(resume.getStatus());
        resume.setStatus("BANNED");
        resume.setBanReason(reason);
        Resume savedResume = resumeRepository.save(resume);

        // Send notification to the user
        sendBanNotification(savedResume, reason);

        return savedResume;
    }

    private void sendBanNotification(Resume resume, String reason) {
        User applicant = resume.getUser();
        Job job = resume.getJob();

        String title = "⚠️ Hồ sơ ứng tuyển bị cấm";
        String content = String.format(
                "Hồ sơ ứng tuyển của bạn cho vị trí \"%s\" đã bị cấm bởi quản trị viên.%s",
                job.getTitle(),
                reason != null && !reason.isEmpty() ? " Lý do: " + reason : "");

        CreateNotificationDTO notificationDTO = new CreateNotificationDTO();
        notificationDTO.setUserId(applicant.getId());
        notificationDTO.setType("RESUME_BANNED");
        notificationDTO.setTitle(title);
        notificationDTO.setContent(content);
        notificationDTO.setRedirectUrl("/my-jobs");

        notificationService.createNotification(notificationDTO);
    }

    @Transactional
    public Resume unbanResume(Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resume not found with id " + id));

        // Restore previous status or fallback to "NOT VIEW"
        String previousStatus = resume.getPreviousStatus();
        resume.setStatus(previousStatus != null ? previousStatus : "NOT VIEW");
        resume.setPreviousStatus(null);
        resume.setBanReason(null);
        return resumeRepository.save(resume);
    }
}
