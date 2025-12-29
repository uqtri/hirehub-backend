package org.example.hirehub.dto.resume;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.example.hirehub.dto.job.JobDetailDTO;
import org.example.hirehub.dto.job.JobSummaryDTO;
import org.example.hirehub.dto.user.UserSummaryDTO;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter

public class ResumeDetailDTO {
    private Long id;
    private String link;
    private String status;
    private String coverLetter;
    private LocalDateTime createdAt;
    private String openAiResumeId;

    private String banReason;

    private UserSummaryDTO user;
    private JobDetailDTO job;
}
