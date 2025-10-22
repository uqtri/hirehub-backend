package org.example.hirehub.service;

import jakarta.mail.MessagingException;
import org.example.hirehub.entity.Token;
import org.example.hirehub.entity.User;
import org.example.hirehub.util.TokenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class AuthService {

    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final TokenService tokenService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    @Value("${frontend.url}")
    private String frontendUrl;

    AuthService(EmailService emailService, TemplateEngine templateEngine, TokenService tokenService, UserService userService, PasswordEncoder passwordEncoder) {
        this.emailService = emailService;
        this.templateEngine = templateEngine;
        this.tokenService = tokenService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public void sendActivationEmail(String email) throws MessagingException {
        Context context = new Context();
        String token = TokenUtil.generateToken(32, true);

        tokenService.save(new Token(token, email, "activation"));

        String activationLink = frontendUrl + "/activation?email=" + email + "&token=" + token;

        context.setVariable("activationLink", activationLink);
        String htmlContent = templateEngine.process("email/activation", context);

        emailService.sendEmail(email, "Kích hoạt tài khoản", htmlContent, true);
    }
    public void sendPasswordResetEmail(String email) throws MessagingException {
        Context context = new Context();
        String token = TokenUtil.generateToken(32, true);

        tokenService.save(new Token(token, email, "reset-password"));

        String resetLink = frontendUrl + "/reset-password?token=" + token + "&email=" + email;
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
}
