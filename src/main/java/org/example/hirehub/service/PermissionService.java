package org.example.hirehub.service;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

import org.example.hirehub.repository.PermissionRepository;
import org.example.hirehub.entity.Permission;


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
