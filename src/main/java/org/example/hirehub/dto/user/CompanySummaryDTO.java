package org.example.hirehub.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class CompanySummaryDTO {
    private String name;
    private String address;
    private String foundedYear;
    private String description;
    private Integer numberOfEmployees;
    private String avatar;
}
