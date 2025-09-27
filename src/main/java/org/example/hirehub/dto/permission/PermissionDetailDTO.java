package org.example.hirehub.dto.permission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hirehub.dto.role.RoleSummaryDTO;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDetailDTO {
    private Long id;
    private String action;
    private String resource;

    private List<RoleSummaryDTO> roles; // Permission → Role (chỉ SummaryDTO)
}