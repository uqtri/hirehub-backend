package org.example.hirehub.controller;

import org.example.hirehub.dto.user.UserDetailDTO;
import org.example.hirehub.mapper.UserMapper;
import org.example.hirehub.repository.UserRepository;
import org.example.hirehub.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")

public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }
    @GetMapping("")
    public List<UserDetailDTO> findAll() {
        return userService.getAllUsers().stream().map(userMapper::toDTO).toList();
    }
}
