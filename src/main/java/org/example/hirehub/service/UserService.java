package org.example.hirehub.service;

import org.example.hirehub.dto.user.UserDetailDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.cloudinary.utils.ObjectUtils;
import org.example.hirehub.dto.user.UpdateUserRequestDTO;
import org.example.hirehub.entity.UserSkill;
import org.example.hirehub.mapper.UserMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.example.hirehub.repository.UserRepository;
import org.example.hirehub.entity.User;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;
    private final SkillService skillService;
    private final LanguageLevelService languageLevelService;
    private final OpenAiFileService openAiFileService;
    private final EmbeddingService embeddingService;

    public UserService(UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            CloudinaryService cloudinaryService,
            SkillService skillService,
            LanguageLevelService languageLevelService,
            OpenAiFileService openAiFileService,
            EmbeddingService embeddingService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.cloudinaryService = cloudinaryService;
        this.skillService = skillService;
        this.languageLevelService = languageLevelService;
        this.openAiFileService = openAiFileService;
        this.embeddingService = embeddingService;
    }

    public Page<User> getAllUsers(String keyword, String province, String role, Pageable pageable) {
        return userRepository.findAll(keyword, province, role, pageable);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {

        User user = userRepository.findByEmail(email);

        return user;
    }

    public User createUser(User user) {
        User savedUser = userRepository.save(user);

        // Generate embedding asynchronously (pass ID to avoid session issues)
        embeddingService.generateUserEmbeddingAsync(savedUser.getId());

        return savedUser;
    }

    public User updateUser(User user) {
        User savedUser = userRepository.save(user);

        // Regenerate embedding asynchronously (pass ID to avoid session issues)
        embeddingService.generateUserEmbeddingAsync(savedUser.getId());

        return savedUser;
    }

    public User updateUserById(Long id, UpdateUserRequestDTO request) throws IOException {
        User user = userRepository.findById(id).orElse(null);
        if (user == null)
            return null;
        if (request.getPassword() != null) {
            request.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        MultipartFile avatar = request.getAvatar();
        if (avatar != null && !avatar.isEmpty()) {
            String url = cloudinaryService.uploadAndGetUrl(avatar, ObjectUtils.emptyMap());
            user.setAvatar(url);
        }
        MultipartFile resume = request.getResume();
        if (resume != null && !resume.isEmpty()) {
            String url = cloudinaryService.uploadAndGetUrl(resume, ObjectUtils.emptyMap());
            String openAiResumeId = openAiFileService.uploadFile(resume);
            user.setOpenAiResumeId(openAiResumeId);
            user.setResume_link(url);
        }
        userMapper.updateUserFromDTO(user, request);
        List<Long> skillIds = request.getSkillIds();
        List<Long> languageLevelIds = request.getLanguageLevelIds();
        if (skillIds != null && !skillIds.isEmpty()) {

            user.getUserSkills().clear();

            skillService.findSkillsByIds(skillIds).forEach(skill -> {
                user.getUserSkills().add(new UserSkill(user, skill));
            });
        }
        if (languageLevelIds != null && !languageLevelIds.isEmpty()) {
            user.getUserSkills().clear();

            user.setLanguages(languageLevelService.findByIds(languageLevelIds));
        }
        User savedUser = userRepository.save(user);

        // Regenerate embedding asynchronously (pass ID to avoid session issues)
        embeddingService.generateUserEmbeddingAsync(savedUser.getId());

        return savedUser;
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return;
        }

        user.setDeleted(true);
        userRepository.save(user);

        // Delete embedding when user is soft-deleted
        embeddingService.deleteUserEmbedding(id);
    }
}
