package org.example.hirehub.service;

import org.example.hirehub.entity.Permission;
import org.example.hirehub.repository.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;
    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }
    public Permission save(Permission permission) {
        return permissionRepository.save(permission);
    }
    public Optional<Permission> getPermissionById(Long id) {
        return permissionRepository.findById(id);
    }
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

}
