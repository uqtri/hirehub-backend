package org.example.hirehub.service;

import jakarta.annotation.Nullable;
import jakarta.mail.MessagingException;
import org.example.hirehub.dto.auth.LoginRequest;
import org.example.hirehub.dto.auth.SignUpRequest;
import org.example.hirehub.dto.user.CreateUserRequestDTO;
import org.example.hirehub.entity.Role;
import org.example.hirehub.entity.Token;
import org.example.hirehub.entity.User;
import org.example.hirehub.exception.AuthHandlerException;
import org.example.hirehub.mapper.AuthMapper;
import org.example.hirehub.mapper.UserMapper;
import org.example.hirehub.repository.UserRepository;
import org.example.hirehub.util.TokenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class AuthService {

    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final TokenService tokenService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;
    private final AuthenticationManager authenticationManager;
    private final RoleService roleService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    @Value("${frontend.url}")
    private String frontendUrl;

    AuthService(EmailService emailService, TemplateEngine templateEngine, TokenService tokenService, UserService userService, PasswordEncoder passwordEncoder, AuthMapper authMapper, AuthenticationManager authenticationManager, RoleService roleService, UserMapper userMapper, UserRepository userRepository) {
        this.emailService = emailService;
        this.templateEngine = templateEngine;
        this.tokenService = tokenService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authMapper = authMapper;
        this.authenticationManager = authenticationManager;
        this.roleService = roleService;
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    public User login(@RequestBody(required = false) LoginRequest loginRequest) {

        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAnonymous =
                currentAuthentication == null ||
                        !currentAuthentication.isAuthenticated() ||
                        currentAuthentication instanceof AnonymousAuthenticationToken;

        if(!isAnonymous) {
            return userService.getUserByEmail(currentAuthentication.getName());
        }
        Authentication authentication = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        Authentication result = authenticationManager.authenticate(authentication);

        if (!result.isAuthenticated()) {
            return null;
        }
        SecurityContextHolder.getContext().setAuthentication(result);
        return userService.getUserByEmail(loginRequest.getEmail());
    }

    public void sendActivationEmail(String email) throws MessagingException {
        Context context = new Context();
        String token = TokenUtil.generateToken(6, false);

        tokenService.save(new Token(token, email, "activation"));

        String activationLink = frontendUrl + "/auth/activation?email=" + email + "&token=" + token;

        context.setVariable("activationLink", activationLink);
        String htmlContent = templateEngine.process("email/activation", context);

        emailService.sendEmail(email, "Kích hoạt tài khoản", htmlContent, true, "HireHub Support");
    }
    public void sendPasswordResetEmail(String email) throws MessagingException {
        Context context = new Context();
        String token = TokenUtil.generateToken(32, true);

        tokenService.save(new Token(token, email, "reset-password"));

        String resetLink = frontendUrl + "/auth/reset-password?token=" + token + "&email=" + email;
        context.setVariable("resetLink", resetLink);
        String htmlContent = templateEngine.process("email/reset-password", context);

        emailService.sendEmail(email, "Thay đổi mật khẩu", htmlContent, true, "HireHub Support");
    }
    public void activate(String token, String email) throws Exception {

        Token code = tokenService.findTokenByIdAndType(token, "activation");
        User user = userService.getUserByEmail(email);

        if(code == null || Duration.between(code.getCreatedAt(), LocalDateTime.now()).toDays() >= 1) {
            throw new Exception("Mã không hợp lệ hoặc đã hết hạn");
        }
        if(user == null) {
            throw new Exception("Không tìm thấy người dùng");
        }
        user.setIsVerified(true);
        userService.save(user);
        tokenService.delete(code);
    }
    public void resetPassword(String token, String email, String newPassword) throws Exception {
        Token code = tokenService.findTokenByIdAndType(token, "reset-password");
        User user = userService.getUserByEmail(email);

        if(code == null || Duration.between(code.getCreatedAt(), LocalDateTime.now()).toDays() >= 1) {
            throw new Exception("Mã không hợp lệ hoặc đã hết hạn");
        }
        if(user == null) {
            throw new Exception("Không tìm thấy người dùng");
        }
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        tokenService.delete(code);
        userService.save(user);
    }
    public User signUp(CreateUserRequestDTO data) throws Exception {
        User user = userService.getUserByEmail(data.getEmail());
        if(user != null) {
            throw new AuthHandlerException.UserAlreadyExistException("Đã tồn tại email");
        }

        data.setPassword(passwordEncoder.encode(data.getPassword()));
        User newUser = userMapper.toEntity(data);
        Role defaultRole = roleService.getRoleByName("user").orElse(null);

        Role role = (data.getRoleId() != null ? roleService.getRoleById(data.getRoleId()).orElse(defaultRole) : defaultRole);
        newUser.setRole(role);
        userService.save(newUser);
        if(role != null && role.getName().equals("user")) this.sendActivationEmail(data.getEmail());
        return newUser;
    }
    public User getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAnonymous = authentication.getPrincipal().equals("anonymousUser");

        if(isAnonymous) {
            return null;
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email);
    }

}
