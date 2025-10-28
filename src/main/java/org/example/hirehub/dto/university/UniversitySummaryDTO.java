package org.example.hirehub.dto.university;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UniversitySummaryDTO {
    private Long id;
    private String name;
    private String logo;
}
