package org.example.hirehub.service;

import org.example.hirehub.dto.interview.*;
import org.example.hirehub.entity.*;
import org.example.hirehub.exception.ResourceNotFoundException;
import org.example.hirehub.message.EmailMessage;
import org.example.hirehub.producer.EmailProducer;
import org.example.hirehub.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InterviewScheduleService {

    private final InterviewScheduleRequestRepository scheduleRequestRepository;
    private final InterviewTimeSlotRepository timeSlotRepository;
    private final InterviewRoomRepository roomRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final InterviewService interviewService;
    private final EmailProducer emailProducer;

    public InterviewScheduleService(
            InterviewScheduleRequestRepository scheduleRequestRepository,
            InterviewTimeSlotRepository timeSlotRepository,
            InterviewRoomRepository roomRepository,
            JobRepository jobRepository,
            UserRepository userRepository,
            InterviewService interviewService,
            EmailProducer emailProducer) {
        this.scheduleRequestRepository = scheduleRequestRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.roomRepository = roomRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.interviewService = interviewService;
        this.emailProducer = emailProducer;
    }

    @Transactional
    public ScheduleRequestDTO createScheduleRequest(CreateScheduleRequestDTO dto) {
        // Validate entities exist
        Job job = jobRepository.findById(dto.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        User applicant = userRepository.findById(dto.getApplicantId())
                .orElseThrow(() -> new ResourceNotFoundException("Applicant not found"));

        User recruiter = userRepository.findById(dto.getRecruiterId())
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));

        // Validate time slots
        if (dto.getProposedTimeSlots() == null || dto.getProposedTimeSlots().isEmpty()) {
            throw new IllegalArgumentException("At least one time slot is required");
        }

        if (dto.getProposedTimeSlots().size() > 10) {
            throw new IllegalArgumentException("Maximum 10 time slots allowed");
        }

        // Generate unique request code
        String requestCode = generateUniqueRequestCode();

        // Create schedule request
        InterviewScheduleRequest request = new InterviewScheduleRequest();
        request.setJob(job);
        request.setApplicant(applicant);
        request.setRecruiter(recruiter);
        request.setRequestCode(requestCode);
        request.setStatus("PENDING");
        request.setInterviewType(dto.getInterviewType() != null ? dto.getInterviewType() : "CHAT");
        request.setInterviewMode(dto.getInterviewMode() != null ? dto.getInterviewMode() : "LIVE");
        request.setRoundNumber(dto.getRoundNumber() != null ? dto.getRoundNumber() : 1);
        request.setPreviousRoomId(dto.getPreviousRoomId());
        request.setCreatedAt(LocalDateTime.now());

        // Set expiration (default 48 hours)
        int expirationHours = dto.getExpirationHours() != null ? dto.getExpirationHours() : 48;
        request.setExpiresAt(LocalDateTime.now().plusHours(expirationHours));

        InterviewScheduleRequest savedRequest = scheduleRequestRepository.save(request);

        // Create time slots and check availability
        List<InterviewTimeSlot> timeSlots = new ArrayList<>();
        for (LocalDateTime proposedTime : dto.getProposedTimeSlots()) {
            InterviewTimeSlot slot = new InterviewTimeSlot();
            slot.setScheduleRequest(savedRequest);
            slot.setProposedTime(proposedTime);
            slot.setCreatedAt(LocalDateTime.now());

            // Check for conflicts
            ConflictCheckResult conflictCheck = checkTimeConflict(applicant.getId(), proposedTime);
            slot.setIsAvailable(!conflictCheck.hasConflict());
            slot.setConflictReason(conflictCheck.getReason());

            timeSlots.add(timeSlotRepository.save(slot));
        }

        // Send email to applicant
        try {
            sendTimeSlotSelectionEmail(savedRequest, timeSlots);
        } catch (Exception e) {
            System.err.println("Failed to send time slot selection email: " + e.getMessage());
            e.printStackTrace();
        }

        return toScheduleRequestDTO(savedRequest, timeSlots);
    }

    public ScheduleRequestDTO getScheduleRequestByCode(String requestCode) {
        InterviewScheduleRequest request = scheduleRequestRepository.findByRequestCode(requestCode)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule request not found"));

        List<InterviewTimeSlot> timeSlots = timeSlotRepository
                .findByScheduleRequestIdOrderByProposedTimeAsc(request.getId());

        // Recalculate availability in case applicant's schedule changed
        for (InterviewTimeSlot slot : timeSlots) {
            ConflictCheckResult conflictCheck = checkTimeConflict(
                    request.getApplicant().getId(),
                    slot.getProposedTime());
            slot.setIsAvailable(!conflictCheck.hasConflict());
            slot.setConflictReason(conflictCheck.getReason());
        }

        return toScheduleRequestDTO(request, timeSlots);
    }

    @Transactional
    public InterviewRoomDTO selectTimeSlot(SelectTimeSlotDTO dto) {
        // Validate schedule request
        InterviewScheduleRequest request = scheduleRequestRepository.findById(dto.getScheduleRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Schedule request not found"));

        // Verify applicant
        if (!request.getApplicant().getId().equals(dto.getApplicantId())) {
            throw new IllegalArgumentException("Unauthorized: You are not the applicant for this interview");
        }

        // Verify status
        if (!"PENDING".equals(request.getStatus())) {
            throw new IllegalStateException("This schedule request is no longer pending");
        }

        // Check expiration
        if (request.getExpiresAt() != null && LocalDateTime.now().isAfter(request.getExpiresAt())) {
            request.setStatus("EXPIRED");
            scheduleRequestRepository.save(request);
            throw new IllegalStateException("This schedule request has expired");
        }

        // Validate time slot
        InterviewTimeSlot selectedSlot = timeSlotRepository.findById(dto.getTimeSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Time slot not found"));

        if (!selectedSlot.getScheduleRequest().getId().equals(request.getId())) {
            throw new IllegalArgumentException("Time slot does not belong to this schedule request");
        }

        // Final availability check
        ConflictCheckResult conflictCheck = checkTimeConflict(
                request.getApplicant().getId(),
                selectedSlot.getProposedTime());

        if (conflictCheck.hasConflict()) {
            throw new IllegalStateException("Selected time slot is no longer available: " + conflictCheck.getReason());
        }

        // Update schedule request
        request.setStatus("SELECTED");
        request.setSelectedTimeSlotId(selectedSlot.getId());
        request.setRespondedAt(LocalDateTime.now());
        scheduleRequestRepository.save(request);

        // Create actual interview room
        CreateInterviewRoomDTO createRoomDTO = new CreateInterviewRoomDTO();
        createRoomDTO.setJobId(request.getJob().getId());
        createRoomDTO.setApplicantId(request.getApplicant().getId());
        createRoomDTO.setRecruiterId(request.getRecruiter().getId());
        createRoomDTO.setScheduledTime(selectedSlot.getProposedTime());
        createRoomDTO.setInterviewType(request.getInterviewType());
        createRoomDTO.setInterviewMode(request.getInterviewMode());
        createRoomDTO.setRoundNumber(request.getRoundNumber());
        createRoomDTO.setPreviousRoomId(request.getPreviousRoomId());

        InterviewRoomDTO room = interviewService.createInterviewRoom(createRoomDTO);

        // Send confirmation email to recruiter
        try {
            sendConfirmationEmailToRecruiter(request, selectedSlot, room);
        } catch (Exception e) {
            System.err.println("Failed to send confirmation email to recruiter: " + e.getMessage());
            e.printStackTrace();
        }

        return room;
    }

    public List<ScheduleRequestDTO> getPendingRequestsByApplicant(Long applicantId) {
        List<InterviewScheduleRequest> requests = scheduleRequestRepository
                .findByApplicantIdAndStatus(applicantId, "PENDING");

        return requests.stream()
                .map(request -> {
                    List<InterviewTimeSlot> timeSlots = timeSlotRepository
                            .findByScheduleRequestIdOrderByProposedTimeAsc(request.getId());
                    return toScheduleRequestDTO(request, timeSlots);
                })
                .collect(Collectors.toList());
    }

    public List<ScheduleRequestDTO> getPendingRequestsByRecruiter(Long recruiterId) {
        List<InterviewScheduleRequest> requests = scheduleRequestRepository
                .findByRecruiterIdAndStatus(recruiterId, "PENDING");

        return requests.stream()
                .map(request -> {
                    List<InterviewTimeSlot> timeSlots = timeSlotRepository
                            .findByScheduleRequestIdOrderByProposedTimeAsc(request.getId());
                    return toScheduleRequestDTO(request, timeSlots);
                })
                .collect(Collectors.toList());
    }

    public List<ScheduleRequestDTO> getAllRequestsByApplicant(Long applicantId) {
        List<InterviewScheduleRequest> requests = scheduleRequestRepository
                .findByApplicantIdOrderByCreatedAtDesc(applicantId);

        return requests.stream()
                .map(request -> {
                    List<InterviewTimeSlot> timeSlots = timeSlotRepository
                            .findByScheduleRequestIdOrderByProposedTimeAsc(request.getId());
                    return toScheduleRequestDTO(request, timeSlots);
                })
                .collect(Collectors.toList());
    }

    public List<ScheduleRequestDTO> getAllRequestsByRecruiter(Long recruiterId) {
        List<InterviewScheduleRequest> requests = scheduleRequestRepository
                .findByRecruiterIdOrderByCreatedAtDesc(recruiterId);

        return requests.stream()
                .map(request -> {
                    List<InterviewTimeSlot> timeSlots = timeSlotRepository
                            .findByScheduleRequestIdOrderByProposedTimeAsc(request.getId());
                    return toScheduleRequestDTO(request, timeSlots);
                })
                .collect(Collectors.toList());
    }

    private ConflictCheckResult checkTimeConflict(Long applicantId, LocalDateTime proposedTime) {
        // Check if applicant has any SCHEDULED or ONGOING interviews
        // within ±2 hours of the proposed time
        LocalDateTime startWindow = proposedTime.minusHours(2);
        LocalDateTime endWindow = proposedTime.plusHours(2);

        List<InterviewRoom> existingRooms = roomRepository.findByApplicantId(applicantId);

        for (InterviewRoom room : existingRooms) {
            if (("SCHEDULED".equals(room.getStatus()) || "ONGOING".equals(room.getStatus()))
                    && room.getScheduledTime() != null) {

                LocalDateTime scheduledTime = room.getScheduledTime();

                if (scheduledTime.isAfter(startWindow) && scheduledTime.isBefore(endWindow)) {
                    String reason = String.format(
                            "Conflicts with existing interview for '%s' at %s",
                            room.getJob().getTitle(),
                            room.getScheduledTime());
                    return new ConflictCheckResult(true, reason);
                }
            }
        }

        return new ConflictCheckResult(false, null);
    }

    private String generateUniqueRequestCode() {
        String requestCode;
        do {
            requestCode = UUID.randomUUID().toString();
        } while (scheduleRequestRepository.existsByRequestCode(requestCode));
        return requestCode;
    }

    private void sendTimeSlotSelectionEmail(InterviewScheduleRequest request, List<InterviewTimeSlot> timeSlots) {
        String selectionLink = "http://localhost:5173/interview-schedule/" + request.getRequestCode();

        StringBuilder timeSlotsText = new StringBuilder();
        for (int i = 0; i < timeSlots.size(); i++) {
            timeSlotsText.append(String.format("%d. %s%s\n",
                    i + 1,
                    timeSlots.get(i).getProposedTime(),
                    timeSlots.get(i).getIsAvailable() ? ""
                            : " (Unavailable - " + timeSlots.get(i).getConflictReason() + ")"));
        }

        String emailContent = String.format(
                "Dear %s,\n\n" +
                        "You have been invited to schedule an interview for the position: %s\n\n" +
                        "Interview Details:\n" +
                        "- Type: %s\n" +
                        "- Mode: %s\n" +
                        "- Round: %d\n" +
                        "- Recruiter: %s\n\n" +
                        "The recruiter has proposed the following time slots:\n\n%s\n" +
                        "Please select your preferred available time slot using this link:\n%s\n\n" +
                        "This request will expire on: %s\n\n" +
                        "Best regards,\n%s\n%s",
                request.getApplicant().getName(),
                request.getJob().getTitle(),
                request.getInterviewType(),
                request.getInterviewMode(),
                request.getRoundNumber(),
                request.getRecruiter().getName(),
                timeSlotsText.toString(),
                selectionLink,
                request.getExpiresAt(),
                request.getRecruiter().getName(),
                request.getRecruiter().getEmail());

        EmailMessage emailMessage = new EmailMessage.Builder()
                .to(request.getApplicant().getEmail())
                .subject("Interview Time Slot Selection - " + request.getJob().getTitle())
                .body(emailContent)
                .type("INTERVIEW_SCHEDULE_REQUEST")
                .isHtml(false)
                .build();

        emailProducer.sendEmail(emailMessage);
    }

    private void sendConfirmationEmailToRecruiter(
            InterviewScheduleRequest request,
            InterviewTimeSlot selectedSlot,
            InterviewRoomDTO room) {
        String emailContent = String.format(
                "Dear %s,\n\n" +
                        "Good news! %s has selected a time slot for the interview.\n\n" +
                        "Interview Details:\n" +
                        "- Position: %s\n" +
                        "- Candidate: %s\n" +
                        "- Selected Time: %s\n" +
                        "- Type: %s\n" +
                        "- Mode: %s\n" +
                        "- Round: %d\n\n" +
                        "Interview Room Link:\nhttp://localhost:5173/interview-room/%s\n\n" +
                        "The interview room has been created and the candidate has been notified.\n\n" +
                        "Best regards,\nHireHub Team",
                request.getRecruiter().getName(),
                request.getApplicant().getName(),
                request.getJob().getTitle(),
                request.getApplicant().getName(),
                selectedSlot.getProposedTime(),
                request.getInterviewType(),
                request.getInterviewMode(),
                request.getRoundNumber(),
                room.getRoomCode());

        EmailMessage emailMessage = new EmailMessage.Builder()
                .to(request.getRecruiter().getEmail())
                .subject("Interview Scheduled - " + request.getApplicant().getName())
                .body(emailContent)
                .type("INTERVIEW_SCHEDULED_CONFIRMATION")
                .isHtml(false)
                .build();

        emailProducer.sendEmail(emailMessage);
    }

    private ScheduleRequestDTO toScheduleRequestDTO(
            InterviewScheduleRequest request,
            List<InterviewTimeSlot> timeSlots) {
        ScheduleRequestDTO dto = new ScheduleRequestDTO();
        dto.setId(request.getId());
        dto.setJobId(request.getJob().getId());
        dto.setJobTitle(request.getJob().getTitle());
        dto.setApplicantId(request.getApplicant().getId());
        dto.setApplicantName(request.getApplicant().getName());
        dto.setApplicantEmail(request.getApplicant().getEmail());
        dto.setRecruiterId(request.getRecruiter().getId());
        dto.setRecruiterName(request.getRecruiter().getName());
        dto.setRecruiterEmail(request.getRecruiter().getEmail());
        dto.setStatus(request.getStatus());
        dto.setInterviewType(request.getInterviewType());
        dto.setInterviewMode(request.getInterviewMode());
        dto.setRoundNumber(request.getRoundNumber());
        dto.setSelectedTimeSlotId(request.getSelectedTimeSlotId());
        dto.setRequestCode(request.getRequestCode());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setExpiresAt(request.getExpiresAt());
        dto.setRespondedAt(request.getRespondedAt());

        List<TimeSlotDTO> timeSlotDTOs = timeSlots.stream()
                .map(this::toTimeSlotDTO)
                .collect(Collectors.toList());
        dto.setTimeSlots(timeSlotDTOs);

        return dto;
    }

    private TimeSlotDTO toTimeSlotDTO(InterviewTimeSlot slot) {
        TimeSlotDTO dto = new TimeSlotDTO();
        dto.setId(slot.getId());
        dto.setProposedTime(slot.getProposedTime());
        dto.setIsAvailable(slot.getIsAvailable());
        dto.setConflictReason(slot.getConflictReason());
        return dto;
    }

    // Inner class for conflict check result
    private static class ConflictCheckResult {
        private final boolean hasConflict;
        private final String reason;

        public ConflictCheckResult(boolean hasConflict, String reason) {
            this.hasConflict = hasConflict;
            this.reason = reason;
        }

        public boolean hasConflict() {
            return hasConflict;
        }

        public String getReason() {
            return reason;
        }
    }
}
