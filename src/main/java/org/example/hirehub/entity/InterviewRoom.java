package org.example.hirehub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "interview_room")
public class InterviewRoom {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;
    
    @ManyToOne
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;
    
    @ManyToOne
    @JoinColumn(name = "recruiter_id", nullable = false)
    private User recruiter;
    
    @Column(nullable = false, unique = true, length = 36)
    private String roomCode;
    
    @Column(nullable = false)
    private LocalDateTime scheduledTime;
    
    @Column(nullable = false)
    private Integer durationMinutes = 60; // Thời lượng phỏng vấn (phút), mặc định 60 phút
    
    @Column(nullable = false, length = 20)
    private String status = "SCHEDULED"; // SCHEDULED, ONGOING, FINISHED, CANCELLED, EXPIRED
    
    @Column(nullable = false, length = 20)
    private String interviewType = "CHAT"; // CHAT, VIDEO
    
    @Column(nullable = false, length = 20)
    private String interviewMode = "LIVE"; // LIVE (recruiter có mặt), ASYNC (tự động)
    
    @Column(nullable = false)
    private Integer roundNumber = 1; // Vòng phỏng vấn thứ mấy
    
    private Long previousRoomId; // ID của phòng phỏng vấn trước (nếu là vòng tiếp theo)
    
    @Column(nullable = false)
    private boolean emailSent = false;
    
    @Column(nullable = false)
    private boolean notificationSent = false;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime startedAt;
    
    private LocalDateTime endedAt;
}

