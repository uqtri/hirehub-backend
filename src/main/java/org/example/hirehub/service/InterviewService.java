package org.example.hirehub.service;

import org.example.hirehub.dto.interview.*;
import org.example.hirehub.entity.*;
import org.example.hirehub.exception.ResourceNotFoundException;
import org.example.hirehub.mapper.InterviewMapper;
import org.example.hirehub.message.EmailMessage;
import org.example.hirehub.producer.EmailProducer;
import org.example.hirehub.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InterviewService {
    
    private final InterviewRoomRepository roomRepository;
    private final InterviewMessageRepository messageRepository;
    private final InterviewResultRepository resultRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final QuestionRepository questionRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final InterviewMapper interviewMapper;
    private final NotificationService notificationService;
    private final EmailProducer emailProducer;
    private final EntityManager entityManager;
    
    public InterviewService(
            InterviewRoomRepository roomRepository,
            InterviewMessageRepository messageRepository,
            InterviewResultRepository resultRepository,
            InterviewQuestionRepository interviewQuestionRepository,
            QuestionRepository questionRepository,
            JobRepository jobRepository,
            UserRepository userRepository,
            InterviewMapper interviewMapper,
            NotificationService notificationService,
            EmailProducer emailProducer,
            EntityManager entityManager
    ) {
        this.roomRepository = roomRepository;
        this.messageRepository = messageRepository;
        this.resultRepository = resultRepository;
        this.interviewQuestionRepository = interviewQuestionRepository;
        this.questionRepository = questionRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.interviewMapper = interviewMapper;
        this.notificationService = notificationService;
        this.emailProducer = emailProducer;
        this.entityManager = entityManager;
    }
    
    @Transactional
    public InterviewRoomDTO createInterviewRoom(CreateInterviewRoomDTO dto) {
        // Validate entities exist
        Job job = jobRepository.findById(dto.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        
        User applicant = userRepository.findById(dto.getApplicantId())
                .orElseThrow(() -> new ResourceNotFoundException("Applicant not found"));
        
        User recruiter = userRepository.findById(dto.getRecruiterId())
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));
        
        // Generate unique room code
        String roomCode = generateUniqueRoomCode();
        
        // Create interview room
        InterviewRoom room = new InterviewRoom();
        room.setJob(job);
        room.setApplicant(applicant);
        room.setRecruiter(recruiter);
        room.setRoomCode(roomCode);
        room.setScheduledTime(dto.getScheduledTime());
        room.setDurationMinutes(dto.getDurationMinutes() != null ? dto.getDurationMinutes() : 60);
        room.setStatus("SCHEDULED");
        room.setInterviewType(dto.getInterviewType() != null ? dto.getInterviewType() : "CHAT");
        room.setInterviewMode(dto.getInterviewMode() != null ? dto.getInterviewMode() : "LIVE");
        room.setRoundNumber(dto.getRoundNumber() != null ? dto.getRoundNumber() : 1);
        room.setPreviousRoomId(dto.getPreviousRoomId());
        room.setCreatedAt(LocalDateTime.now());
        
        InterviewRoom savedRoom = roomRepository.save(room);
        
        // Flush to ensure room is persisted before creating related entities
        entityManager.flush();
        
        // Create interview questions for ASYNC mode
        if ("ASYNC".equals(savedRoom.getInterviewMode()) && dto.getSelectedQuestionIds() != null) {
            for (int i = 0; i < dto.getSelectedQuestionIds().size(); i++) {
                Long questionId = dto.getSelectedQuestionIds().get(i);
                Question question = questionRepository.findById(questionId)
                        .orElseThrow(() -> new ResourceNotFoundException("Question not found: " + questionId));
                
                InterviewQuestion interviewQuestion = new InterviewQuestion();
                interviewQuestion.setRoom(savedRoom);
                interviewQuestion.setQuestion(question);
                interviewQuestion.setQuestionContent(question.getContent());
                interviewQuestion.setOrderIndex(i);
                interviewQuestion.setAskedAt(LocalDateTime.now());
                interviewQuestion.setStatus("PENDING");
                
                interviewQuestionRepository.save(interviewQuestion);
            }
        }
        
        // Create system message (before sending notifications to ensure it's in transaction)
        createSystemMessage(savedRoom, "Interview room created. Waiting for participants to join.");
        
        // Send notification and email (after transaction commits, won't affect room creation if fails)
        try {
            sendInterviewInvitation(savedRoom);
        } catch (Exception e) {
            // Log error but don't fail the transaction
            System.err.println("Failed to send invitation (room created successfully): " + e.getMessage());
            e.printStackTrace();
        }
        
        return enrichRoomDTO(interviewMapper.toRoomDTO(savedRoom), savedRoom);
    }
    
    private void sendInterviewInvitation(InterviewRoom room) {
        try {
            String roomLink = "http://localhost:5173/interview-room/" + room.getRoomCode();
            
            // Send email using EmailMessage
            String emailContent = String.format(
                "Dear %s,\n\n" +
                "You have been invited to an interview for the position: %s\n\n" +
                "Interview Details:\n" +
                "- Type: %s\n" +
                "- Mode: %s\n" +
                "- Round: %d\n" +
                "- Scheduled Time: %s\n\n" +
                "Please join the interview room using this link:\n%s\n\n" +
                "Best regards,\n%s\n%s",
                room.getApplicant().getName(),
                room.getJob().getTitle(),
                room.getInterviewType(),
                room.getInterviewMode(),
                room.getRoundNumber(),
                room.getScheduledTime(),
                roomLink,
                room.getRecruiter().getName(),
                room.getRecruiter().getEmail()
            );
            
            EmailMessage emailMessage = new EmailMessage.Builder()
                    .to(room.getApplicant().getEmail())
                    .subject("Interview Invitation - " + room.getJob().getTitle())
                    .body(emailContent)
                    .type("INTERVIEW_INVITATION")
                    .isHtml(false)
                    .build();
            
            emailProducer.sendEmail(emailMessage);
            
            room.setNotificationSent(true);
            room.setEmailSent(true);
            roomRepository.save(room);
        } catch (Exception e) {
            System.err.println("Error sending invitation: " + e.getMessage());
        }
    }
    
    private String generateUniqueRoomCode() {
        String roomCode;
        do {
            roomCode = UUID.randomUUID().toString();
        } while (roomRepository.existsByRoomCode(roomCode));
        return roomCode;
    }
    
    public InterviewRoomDTO getRoomByCode(String roomCode) {
        InterviewRoom room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException("Interview room not found"));
        return enrichRoomDTO(interviewMapper.toRoomDTO(room), room);
    }
    
    public InterviewRoomDTO getRoomById(Long id) {
        InterviewRoom room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview room not found"));
        return enrichRoomDTO(interviewMapper.toRoomDTO(room), room);
    }
    
    public List<InterviewRoomDTO> getRoomsByRecruiterId(Long recruiterId) {
        return roomRepository.findByRecruiterId(recruiterId)
                .stream()
                .map(room -> enrichRoomDTO(interviewMapper.toRoomDTO(room), room))
                .collect(Collectors.toList());
    }
    
    public List<InterviewRoomDTO> getRoomsByApplicantId(Long applicantId) {
        return roomRepository.findByApplicantId(applicantId)
                .stream()
                .map(room -> enrichRoomDTO(interviewMapper.toRoomDTO(room), room))
                .collect(Collectors.toList());
    }
    
    // Helper method to enrich DTO with calculated fields
    private InterviewRoomDTO enrichRoomDTO(InterviewRoomDTO dto, InterviewRoom room) {
        dto.setExpired(isRoomExpired(room));
        return dto;
    }
    
    // Check if room is expired based on scheduled time + duration
    private boolean isRoomExpired(InterviewRoom room) {
        if (room.getScheduledTime() == null || room.getDurationMinutes() == null) {
            return false;
        }
        LocalDateTime expirationTime = room.getScheduledTime().plusMinutes(room.getDurationMinutes());
        return LocalDateTime.now().isAfter(expirationTime);
    }
    
    @Transactional
    public InterviewRoomDTO updateRoomStatus(String roomCode, String status) {
        InterviewRoom room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException("Interview room not found"));
        
        room.setStatus(status);
        
        if ("ONGOING".equals(status) && room.getStartedAt() == null) {
            room.setStartedAt(LocalDateTime.now());
            createSystemMessage(room, "Interview has started.");
        } else if ("FINISHED".equals(status) && room.getEndedAt() == null) {
            room.setEndedAt(LocalDateTime.now());
            createSystemMessage(room, "Interview has ended.");
        }
        
        InterviewRoom savedRoom = roomRepository.save(room);
        return enrichRoomDTO(interviewMapper.toRoomDTO(savedRoom), savedRoom);
    }
    
    @Transactional
    public InterviewRoomDTO extendInterviewDuration(String roomCode, Integer additionalMinutes) {
        InterviewRoom room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException("Interview room not found"));
        
        // Check if room is already expired or finished
        if (isRoomExpired(room) || "FINISHED".equals(room.getStatus())) {
            throw new IllegalStateException("Cannot extend interview: Room is already expired or finished");
        }
        
        // Extend duration
        int currentDuration = room.getDurationMinutes() != null ? room.getDurationMinutes() : 60;
        room.setDurationMinutes(currentDuration + additionalMinutes);
        
        // Create system message
        createSystemMessage(room, String.format("Interview duration extended by %d minutes.", additionalMinutes));
        
        InterviewRoom savedRoom = roomRepository.save(room);
        return enrichRoomDTO(interviewMapper.toRoomDTO(savedRoom), savedRoom);
    }
    
    @Transactional
    public InterviewMessageDTO createMessage(CreateInterviewMessageDTO dto) {
        InterviewRoom room = roomRepository.findByRoomCode(dto.getRoomCode())
                .orElseThrow(() -> new ResourceNotFoundException("Interview room not found"));
        
        User sender = userRepository.findById(dto.getSenderId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        InterviewMessage message = new InterviewMessage();
        message.setRoom(room);
        message.setSender(sender);
        message.setSenderRole(dto.getSenderRole());
        message.setType(dto.getType());
        message.setContent(dto.getContent());
        message.setTimestamp(LocalDateTime.now());
        
        InterviewMessage savedMessage = messageRepository.save(message);
        return interviewMapper.toMessageDTO(savedMessage);
    }
    
    private void createSystemMessage(InterviewRoom room, String content) {
        try {
            InterviewMessage message = new InterviewMessage();
            message.setRoom(room);
            message.setSender(room.getRecruiter()); // System messages use recruiter as sender
            message.setSenderRole("SYSTEM");
            message.setType("SYSTEM");
            message.setContent(content);
            message.setTimestamp(LocalDateTime.now());
            messageRepository.save(message);
        } catch (Exception e) {
            System.err.println("Failed to create system message: " + e.getMessage());
            e.printStackTrace();
            // Don't rethrow - system messages are not critical
        }
    }
    
    public List<InterviewMessageDTO> getMessagesByRoomCode(String roomCode) {
        InterviewRoom room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException("Interview room not found"));
        
        return messageRepository.findByRoomIdOrderByTimestampAsc(room.getId())
                .stream()
                .map(interviewMapper::toMessageDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public InterviewResultDTO submitResult(CreateInterviewResultDTO dto) {
        InterviewRoom room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Interview room not found"));
        
        // Check if result already exists
        if (resultRepository.findByRoomId(room.getId()).isPresent()) {
            throw new IllegalStateException("Interview result already submitted");
        }
        
        InterviewResult result = new InterviewResult();
        result.setRoom(room);
        result.setScore(dto.getScore());
        result.setComment(dto.getComment());
        result.setPrivateNotes(dto.getPrivateNotes());
        result.setRecommendation(dto.getRecommendation());
        result.setCreatedAt(LocalDateTime.now());
        
        InterviewResult savedResult = resultRepository.save(result);
        
        // Update room status to FINISHED
        room.setStatus("FINISHED");
        room.setEndedAt(LocalDateTime.now());
        roomRepository.save(room);
        
        return interviewMapper.toResultDTO(savedResult);
    }
    
    public InterviewResultDTO getResultByRoomId(Long roomId) {
        InterviewResult result = resultRepository.findByRoomId(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview result not found"));
        return interviewMapper.toResultDTO(result);
    }
    
    @Transactional
    public boolean validateUserAccess(String roomCode, Long userId) {
        InterviewRoom room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException("Interview room not found"));
        
        // Check if user is authorized (applicant or recruiter)
        boolean isAuthorized = room.getApplicant().getId().equals(userId) || 
                               room.getRecruiter().getId().equals(userId);
        
        if (!isAuthorized) {
            return false;
        }
        
        // Auto-update status to EXPIRED if time has passed
        if (isRoomExpired(room) && !"FINISHED".equals(room.getStatus()) && !"CANCELLED".equals(room.getStatus())) {
            room.setStatus("EXPIRED");
            room.setEndedAt(room.getScheduledTime().plusMinutes(room.getDurationMinutes()));
            roomRepository.save(room);
        }
        
        // Allow access to view expired/finished rooms (read-only mode will be handled in frontend)
        // Only block CANCELLED rooms
        return !"CANCELLED".equals(room.getStatus());
    }
    
    // New methods for async interview
    public List<InterviewQuestionDTO> getInterviewQuestions(Long roomId) {
        return interviewQuestionRepository.findByRoomIdOrderByOrderIndexAsc(roomId)
                .stream()
                .map(this::toQuestionDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public InterviewQuestionDTO answerQuestion(Long questionId, AnswerQuestionDTO dto) {
        InterviewQuestion question = interviewQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        
        question.setAnswer(dto.getAnswer());
        question.setAnsweredAt(LocalDateTime.now());
        question.setStatus("ANSWERED");
        
        InterviewQuestion saved = interviewQuestionRepository.save(question);
        
        // Check if all questions are answered
        long pendingCount = interviewQuestionRepository.countByRoomIdAndStatus(
                question.getRoom().getId(), "PENDING");
        
        if (pendingCount == 0) {
            // All questions answered, notify recruiter
//            CreateNotificationDTO notificationDTO = CreateNotificationDTO.builder()
//                    .userId(question.getRoom().getRecruiter().getId())
//                    .type("INTERVIEW_COMPLETED")
//                    .title("Interview Completed")
//                    .content(question.getRoom().getApplicant().getName() + " has completed the interview")
//                    .redirectUrl("/recruiter/interviews/" + question.getRoom().getId())
//                    .build();
//
//            notificationService.createNotification(notificationDTO);
        }
        
        return toQuestionDTO(saved);
    }
    
    @Transactional
    public InterviewResultDTO submitAsyncResult(CreateInterviewResultDTO dto) {
        InterviewRoom room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Interview room not found"));
        
        // Check if result already exists
        if (resultRepository.findByRoomId(room.getId()).isPresent()) {
            throw new IllegalStateException("Interview result already submitted");
        }
        
        InterviewResult result = new InterviewResult();
        result.setRoom(room);
        result.setScore(dto.getScore());
        result.setComment(dto.getComment());
        result.setPrivateNotes(dto.getPrivateNotes());
        result.setRecommendation(dto.getRecommendation());
        result.setCreatedAt(LocalDateTime.now());
        
        InterviewResult savedResult = resultRepository.save(result);
        
        // Update room status to FINISHED
        room.setStatus("FINISHED");
        room.setEndedAt(LocalDateTime.now());
        roomRepository.save(room);
        
        // Send result email to applicant
        sendResultEmail(room, savedResult);
        
        return interviewMapper.toResultDTO(savedResult);
    }
    
    private void sendResultEmail(InterviewRoom room, InterviewResult result) {
        try {
            String emailContent = String.format(
                "Dear %s,\n\n" +
                "Thank you for participating in the interview for the position: %s\n\n" +
                "Interview Result: %s\n\n" +
                "Feedback:\n%s\n\n" +
                "%s\n\n" +
                "Best regards,\n%s\n%s",
                room.getApplicant().getName(),
                room.getJob().getTitle(),
                result.getRecommendation(),
                result.getComment(),
                "PASS".equals(result.getRecommendation()) 
                    ? "Congratulations! We will contact you soon for the next steps."
                    : "We appreciate your time and wish you the best in your job search.",
                room.getRecruiter().getName(),
                room.getRecruiter().getEmail()
            );
            
            EmailMessage emailMessage = new EmailMessage.Builder()
                    .to(room.getApplicant().getEmail())
                    .subject("Interview Result - " + room.getJob().getTitle())
                    .body(emailContent)
                    .type("INTERVIEW_RESULT")
                    .isHtml(false)
                    .build();
            
            emailProducer.sendEmail(emailMessage);
            
            // Also create notification
//            CreateNotificationDTO notificationDTO = CreateNotificationDTO.builder()
//                    .userId(room.getApplicant().getId())
//                    .type("INTERVIEW_RESULT")
//                    .title("Interview Result")
//                    .content("Your interview result for " + room.getJob().getTitle() + " is ready")
//                    .redirectUrl("/interviews/" + room.getId())
//                    .build();
//
//            notificationService.createNotification(notificationDTO);
        } catch (Exception e) {
            System.err.println("Error sending result email: " + e.getMessage());
        }
    }
    
    private InterviewQuestionDTO toQuestionDTO(InterviewQuestion question) {
        InterviewQuestionDTO dto = new InterviewQuestionDTO();
        dto.setId(question.getId());
        dto.setRoomId(question.getRoom().getId());
        dto.setQuestionId(question.getQuestion() != null ? question.getQuestion().getId() : null);
        dto.setQuestionContent(question.getQuestionContent());
        dto.setAnswer(question.getAnswer());
        dto.setOrderIndex(question.getOrderIndex());
        dto.setAskedAt(question.getAskedAt());
        dto.setAnsweredAt(question.getAnsweredAt());
        dto.setStatus(question.getStatus());
        dto.setEvaluation(question.getEvaluation());
        return dto;
    }
    
    @Transactional
    public InterviewQuestionDTO evaluateQuestion(Long questionId, EvaluateQuestionDTO dto) {
        InterviewQuestion question = interviewQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        
        // Validate evaluation value
        if (!"PASS".equals(dto.getEvaluation()) && !"FAIL".equals(dto.getEvaluation())) {
            throw new IllegalArgumentException("Evaluation must be either PASS or FAIL");
        }
        
        question.setEvaluation(dto.getEvaluation());
        InterviewQuestion saved = interviewQuestionRepository.save(question);
        
        return toQuestionDTO(saved);
    }
}

