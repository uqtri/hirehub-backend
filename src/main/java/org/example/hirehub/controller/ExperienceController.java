package org.example.hirehub.controller;

import org.example.hirehub.dto.experience.CreateExperienceForm;
import org.example.hirehub.dto.experience.ExperienceDetailDTO;
import org.example.hirehub.dto.experience.UpdateExperienceForm;
import org.example.hirehub.entity.Experience;
import org.example.hirehub.entity.User;
import org.example.hirehub.mapper.ExperienceMapper;
import org.example.hirehub.service.CloudinaryService;
import org.example.hirehub.service.ExperienceService;
import org.example.hirehub.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/experiences")
public class ExperienceController {

    private final ExperienceMapper experienceMapper;
    private final ExperienceService  experienceService;
    private final CloudinaryService cloudinaryService;
    private final UserService userService;

    ExperienceController(ExperienceMapper experienceMapper, CloudinaryService cloudinaryService, ExperienceService experienceService, UserService userService) {
        this.experienceMapper = experienceMapper;
        this.cloudinaryService = cloudinaryService;
        this.experienceService = experienceService;
        this.userService = userService;
    }
    @GetMapping("/userId/{id}")
    public List<ExperienceDetailDTO> getExperiencesByUserId (@PathVariable Long id) {
        return experienceService.getExperiencesByUserId(id).stream().map(experienceMapper::toDTO).toList();
    }

    @PostMapping("")
    public ResponseEntity<Map<String, ?>> createExperience(@ModelAttribute CreateExperienceForm form) throws IOException {
        Experience experience = experienceMapper.toEntity(form);
        MultipartFile file = form.getImage();

        Long companyId = form.getCompanyId();
        Long userId = form.getUserId();
        User company = userService.getUserById(companyId);
        User user = userService.getUserById(userId);

        if(user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Không tìm thấy người dùng"));
        }
        if(company == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Không tìm thấy công ty"));
        }

        experience.setCompany(company);
        experience.setUser(user);
        if (file != null && !file.isEmpty()) {
            String secureUrl = cloudinaryService.uploadAndGetUrl(file);
            experience.setImage(secureUrl);
        }
        experienceService.save(experience);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Tạo kinh nghiệm thành công"));
    }

    @DeleteMapping("/{id}") public ResponseEntity<Map<String, ?>> deleteExperience(@PathVariable Long id) {

        Experience experience = experienceService.findById(id).orElseThrow( () -> new RuntimeException("Experience not found"));
        experience.setDeleted(true);
        experienceService.save(experience);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Experience deleted successfully"));
    }
    @PutMapping("/{id}") public ResponseEntity<Map<String, ?>> updateExperience(@PathVariable Long id, @ModelAttribute UpdateExperienceForm form) throws IOException {

        Experience experience = experienceService.findById(id).orElseThrow( () -> new RuntimeException("Experience not found"));
        experienceMapper.updateFromForm(experience, form);

        MultipartFile file = form.getImage();
        Long companyId = form.getCompanyId();
        Long userId = form.getUserId();

        Optional.ofNullable(companyId).map(userService::getUserById).ifPresent(experience::setCompany);
        Optional.ofNullable(userId).map(userService::getUserById).ifPresent(experience::setUser);

        if (file != null && !file.isEmpty()) {
            String secureUrl= cloudinaryService.uploadAndGetUrl(file);
            experience.setImage(secureUrl);
        }
        experienceService.save(experience);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Experience updated successfully"));
    }
}
