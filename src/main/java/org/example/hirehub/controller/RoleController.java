package org.example.hirehub.controller;

import org.example.hirehub.dto.role.RoleDetailDTO;
import org.example.hirehub.mapper.RoleMapper;
import org.example.hirehub.repository.RoleRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@RestController

@RequestMapping("/api/roles")
public class RoleController {

    private final RoleMapper roleMapper;
    private final RoleRepository roleRepository;
    public RoleController(RoleMapper roleMapper, RoleRepository roleRepository) {
        this.roleMapper = roleMapper;
        this.roleRepository = roleRepository;
    }
    @GetMapping("")
    public List<RoleDetailDTO> getRoles() {
        return roleRepository.findAll().stream().map(roleMapper::toDTO).toList();
    }
}
