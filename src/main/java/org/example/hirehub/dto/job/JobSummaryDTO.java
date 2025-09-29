package org.example.hirehub.dto.job;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hirehub.dto.user.CompanySummaryDTO;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JobSummaryDTO {
    private Long id;
    private String level;
    private String description;
}