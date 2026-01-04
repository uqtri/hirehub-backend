package org.example.hirehub.dto.interview;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequestDTO {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private Long applicantId;
    private String applicantName;
    private String applicantEmail;
    private Long recruiterId;
    private String recruiterName;
    private String recruiterEmail;
    private String status;
    private String interviewType;
    private String interviewMode;
    private Integer roundNumber;
    private List<TimeSlotDTO> timeSlots;
    private Long selectedTimeSlotId;
    private String requestCode;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime respondedAt;
}
