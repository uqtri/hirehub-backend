package org.example.hirehub.dto.job;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

public class JobSummaryDTO {
    private Long id;
    private String title;
    private String level;
    private String description;
    private String address;

}