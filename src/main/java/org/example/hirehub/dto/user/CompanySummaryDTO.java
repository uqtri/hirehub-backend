package org.example.hirehub.dto.user;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter

public class CompanySummaryDTO {
    private String name;
    private String address;
    private String foundedYear;
    private String description;
    private Integer numberOfEmployees;
    private String avatar;
}
