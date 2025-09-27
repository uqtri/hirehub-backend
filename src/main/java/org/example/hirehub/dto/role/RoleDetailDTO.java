package org.example.hirehub.dto.role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hirehub.dto.permission.PermissionSummaryDTO;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoleDetailDTO {
    private Long id;
    private String name;

    private List<PermissionSummaryDTO> permissions;

}