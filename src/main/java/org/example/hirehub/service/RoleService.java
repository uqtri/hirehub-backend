package org.example.hirehub.service;

import org.springframework.stereotype.Service;

import org.example.hirehub.repository.RoleRepository;
import org.example.hirehub.entity.Role;

import java.util.Optional;
import java.util.List;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
    public Optional<Role> getRoleById(Long id) {
        return  roleRepository.findById(id);
    }
    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    public Role save(Role role) {
        return roleRepository.save(role);
    }

}
