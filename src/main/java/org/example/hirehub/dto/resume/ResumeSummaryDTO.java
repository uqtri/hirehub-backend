package org.example.hirehub.dto.resume;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.example.hirehub.dto.user.UserSummaryDTO;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter

public class ResumeSummaryDTO {
    private Long id;
    private String link;
    private String status;
    private String coverLetter;
    private UserSummaryDTO user;
    private String openAiResumeId;


}
