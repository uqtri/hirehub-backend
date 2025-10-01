package org.example.hirehub.dto.role;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.example.hirehub.dto.permission.PermissionSummaryDTO;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

public class RoleDetailDTO {
    private Long id;
    private String name;

    private List<PermissionSummaryDTO> permissions;

}