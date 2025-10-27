package org.example.hirehub.service;

import jakarta.annotation.Nullable;
import jakarta.mail.MessagingException;
import org.example.hirehub.dto.auth.LoginRequest;
import org.example.hirehub.dto.auth.SignUpRequest;
import org.example.hirehub.entity.Token;
import org.example.hirehub.entity.User;
import org.example.hirehub.exception.AuthHandlerException;
import org.example.hirehub.mapper.AuthMapper;
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
    @Value("${frontend.url}")
    private String frontendUrl;

    AuthService(EmailService emailService, TemplateEngine templateEngine, TokenService tokenService, UserService userService, PasswordEncoder passwordEncoder, AuthMapper authMapper, AuthenticationManager authenticationManager) {
        this.emailService = emailService;
        this.templateEngine = templateEngine;
        this.tokenService = tokenService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authMapper = authMapper;
        this.authenticationManager = authenticationManager;
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
        String token = TokenUtil.generateToken(32, true);

        tokenService.save(new Token(token, email, "activation"));

        String activationLink = frontendUrl + "/auth/activation?email=" + email + "&token=" + token;

        context.setVariable("activationLink", activationLink);
        String htmlContent = templateEngine.process("email/activation", context);

        emailService.sendEmail(email, "Kích hoạt tài khoản", htmlContent, true);
    }
    public void sendPasswordResetEmail(String email) throws MessagingException {
        Context context = new Context();
        String token = TokenUtil.generateToken(32, true);

        tokenService.save(new Token(token, email, "reset-password"));

        String resetLink = frontendUrl + "/auth/reset-password?token=" + token + "&email=" + email;
        context.setVariable("resetLink", resetLink);
        String htmlContent = templateEngine.process("email/reset-password", context);

        emailService.sendEmail(email, "Thay đổi mật khẩu", htmlContent, true);
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
    public void signUp(SignUpRequest data) throws Exception {

        if(data.getPassword() == null) {
            throw new AuthHandlerException.PasswordMismatchException("Vui long cung cấp mật khẩu");
        }

        if(!data.getPassword().equals(data.getConfirmPassword())) {
            throw new AuthHandlerException.PasswordMismatchException("Mật khẩu không khớp");
        }
        User user = authMapper.toEntity(data);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
    }
}
