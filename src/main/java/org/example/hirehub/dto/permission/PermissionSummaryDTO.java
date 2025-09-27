package org.example.hirehub.dto.permission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PermissionSummaryDTO {
    private Long id;
    private String action;
    private String resource;
}
