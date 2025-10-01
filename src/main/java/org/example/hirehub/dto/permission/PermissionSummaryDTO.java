package org.example.hirehub.dto.permission;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter

public class PermissionSummaryDTO {
    private Long id;
    private String action;
    private String resource;
}
