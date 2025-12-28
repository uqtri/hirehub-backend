package org.example.hirehub.controller;

import org.example.hirehub.repository.LanguageLevelRepository;
import org.example.hirehub.service.CloudinaryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.persistence.EntityManager;

import org.example.hirehub.dto.user.CreateUserRequestDTO;
import org.example.hirehub.dto.user.UpdateUserRequestDTO;
import org.example.hirehub.repository.SkillRepository;
import org.example.hirehub.dto.user.UserDetailDTO;
import org.example.hirehub.service.UserService;
import org.example.hirehub.service.RoleService;
import org.example.hirehub.mapper.UserMapper;
import org.example.hirehub.entity.UserSkill;
import org.example.hirehub.entity.Role;
import org.example.hirehub.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")

public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final SkillRepository skillRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;
    private final LanguageLevelRepository languageLevelRepository;

    public UserController(UserService userService, UserMapper userMapper, EntityManager entityManager,
            RoleService roleService, SkillRepository skillRepository, PasswordEncoder passwordEncoder,
            CloudinaryService cloudinaryService, LanguageLevelRepository languageLevelRepository) {
        this.userService = userService;
        this.userMapper = userMapper;
        // this.entityManager = entityManager;
        this.roleService = roleService;
        this.skillRepository = skillRepository;
        this.passwordEncoder = passwordEncoder;
        this.cloudinaryService = cloudinaryService;
        this.languageLevelRepository = languageLevelRepository;
    }

    @GetMapping("")
    public Page<UserDetailDTO> findAllUsers(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.DESC) Pageable pageable) {
        return userService.getAllUsers(keyword, province, role, status, pageable).map(userMapper::toDTO);
    }

    @GetMapping("/{id}")
    public UserDetailDTO findUserById(@PathVariable Long id) {
        return userMapper.toDTO(userService.getUserById(id));
    }

    @GetMapping("/email/{email}")
    public UserDetailDTO findUserByEmail(@PathVariable String email) {
        return userMapper.toDTO(userService.getUserByEmail(email));
    }

    @PostMapping("")
    public ResponseEntity<Map<String, ?>> createUser(@RequestBody CreateUserRequestDTO request) {

        try {
            User user = userService.getUserByEmail(request.getEmail());
            if (user != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Người dùng đã tồn tại"));
            }

            request.setPassword(passwordEncoder.encode(request.getPassword()));
            User newUser = userMapper.toEntity(request);
            Role defaultRole = roleService.getRoleByName("User").orElse(null);

            Role role = roleService.getRoleById(request.getRoleId()).orElse(defaultRole);
            newUser.setRole(role);

            userService.save(newUser);
            return ResponseEntity.status(201).body(Map.of("message", "User added"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, ?>> updateUser(@PathVariable Long id,
            @ModelAttribute UpdateUserRequestDTO request) throws IOException {

        User user = userService.updateUserById(id, request);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Không tìm thấy người dùng"));
        }
        return ResponseEntity.status(201)
                .body(Map.of("message", "Cập nhập thông tin thành công", "data", userMapper.toDTO(user)));
    }

    // Admin endpoint to update user status (ban/verify) - accepts JSON
    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, ?>> updateUserStatus(@PathVariable Long id,
            @RequestBody UpdateUserRequestDTO request) throws IOException {
        User user = userService.getUserById(id);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Không tìm thấy người dùng"));
        }

        // Only update ban and verify status
        if (request.getIsBanned() != null) {
            user.setIsBanned(request.getIsBanned());
        }
        if (request.getIsVerified() != null) {
            user.setIsVerified(request.getIsVerified());
        }

        User savedUser = userService.save(user);
        return ResponseEntity.status(200)
                .body(Map.of("message", "Cập nhập trạng thái thành công", "data", userMapper.toDTO(savedUser)));
    }

}
