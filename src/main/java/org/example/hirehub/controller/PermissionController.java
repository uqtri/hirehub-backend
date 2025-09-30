package org.example.hirehub.controller;

import org.example.hirehub.dto.permission.PermissionDetailDTO;
import org.example.hirehub.entity.Permission;
import org.example.hirehub.entity.Role;
import org.example.hirehub.entity.RolePermission;
import org.example.hirehub.mapper.PermissionMapper;
import org.example.hirehub.repository.PermissionRepository;
import org.example.hirehub.service.PermissionService;
import org.example.hirehub.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final PermissionMapper permissionMapper;
    private final PermissionService permissionService;
    private final RoleService roleService;

    PermissionController(PermissionMapper permissionMapper, PermissionService permissionService, RoleService roleService) {

        this.permissionMapper = permissionMapper;
        this.permissionService = permissionService;
        this.roleService = roleService;
    }
    @GetMapping("")
    public List<PermissionDetailDTO> getAllPermisisons() {
        return permissionService.getAllPermissions().stream().map(permissionMapper::toDTO).toList();
    }

    @PostMapping("/grant-permission")
    public ResponseEntity<Map<String, String>> grantPermissionForRole(@RequestParam Long roleId, @RequestParam Long permissionId) {

        Permission permission = permissionService.getPermissionById(permissionId).orElse(null);
        Role role = roleService.getRoleById(roleId).orElse(null);

        if(permission == null || role == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Không tìm thấy vai trò hoặc quyền"));
        }

        List<RolePermission> rolePermission = permission.getRolePermission();

        Optional<RolePermission> existing = permission.getRolePermission().stream()
                .filter(rp -> rp.getRole().getId().equals(roleId)
                        && rp.getPermission().getId().equals(permissionId))
                .findFirst();

        if(existing.isPresent()) {
            rolePermission.remove(existing.get());
            permissionService.save(permission);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Hủy quyền thành công"));
        }
        else {
            RolePermission rp = new RolePermission(role, permission);
            rolePermission.add(rp);
            permissionService.save(permission);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Cấp quyền thành công"));
        }
    }
}
