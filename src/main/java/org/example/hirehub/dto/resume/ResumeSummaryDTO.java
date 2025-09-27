package org.example.hirehub.dto.resume;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hirehub.dto.user.UserSummaryDTO;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResumeSummaryDTO {
    private Long id;
    private String link;
    private String status;
    private String coverLetter;
    private UserSummaryDTO user;

}
