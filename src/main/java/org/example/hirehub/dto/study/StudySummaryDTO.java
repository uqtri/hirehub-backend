package org.example.hirehub.dto.study;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hirehub.dto.university.UniversitySummaryDTO;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudySummaryDTO {
    private Long id;
    private UniversitySummaryDTO university;
    private LocalDate startDate;
    private LocalDate endDate;
    private String major;
    private String degree;
}
