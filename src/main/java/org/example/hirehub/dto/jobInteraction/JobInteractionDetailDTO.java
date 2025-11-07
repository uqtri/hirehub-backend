package org.example.hirehub.dto.jobInteraction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hirehub.dto.job.JobDetailDTO;
import org.example.hirehub.dto.job.JobSummaryDTO;
import org.example.hirehub.dto.user.UserSummaryDTO;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JobInteractionDetailDTO {

    Long id;
    UserSummaryDTO user;
    JobDetailDTO job;
    String interaction;
}
