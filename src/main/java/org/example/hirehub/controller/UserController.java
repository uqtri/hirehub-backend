package org.example.hirehub.controller;

import org.example.hirehub.dto.user.UserDetailDTO;
import org.example.hirehub.mapper.UserMapper;
import org.example.hirehub.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserController(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }
    @GetMapping("")
    public List<UserDetailDTO> findAll() {

        return userRepository.findAllWithRoleAndPermissions().stream().map(userMapper::toDTO).toList();

    }
}
