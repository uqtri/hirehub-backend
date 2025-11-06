package org.example.hirehub.service;

import jakarta.annotation.Nullable;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.hirehub.dto.auth.LoginRequest;
import org.example.hirehub.dto.auth.SignUpRequest;
import org.example.hirehub.dto.user.CreateUserRequestDTO;
import org.example.hirehub.entity.Role;
import org.example.hirehub.entity.Token;
import org.example.hirehub.entity.User;
import org.example.hirehub.exception.AuthHandlerException;
import org.example.hirehub.mapper.AuthMapper;
import org.example.hirehub.mapper.UserMapper;
import org.example.hirehub.message.EmailMessage;
import org.example.hirehub.producer.EmailProducer;
import org.example.hirehub.repository.UserRepository;
import org.example.hirehub.util.TokenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
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
import java.util.*;
import java.util.stream.Collectors;

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
    private final EmailProducer emailProducer;
    private final HttpClientService httpClientService;
    private final JwtService jwtService;
    private final RedisService redisService;
    @Value("${frontend.url}")
    private String frontendUrl;

    @Value("${google.client-id}")
    private String googleClientId;
    @Value("${google.client-secret}")
    private String googleClientSecret;

    @Value("${google.client-redirect-url}")
    private String redirectUrl;

    AuthService(EmailService emailService, TemplateEngine templateEngine, TokenService tokenService, UserService userService, PasswordEncoder passwordEncoder, AuthMapper authMapper, AuthenticationManager authenticationManager, RoleService roleService, UserMapper userMapper, UserRepository userRepository, EmailProducer emailProducer, HttpClientService httpClientService, JwtService jwtService, RedisService redisService) {
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
        this.emailProducer = emailProducer;
        this.httpClientService = httpClientService;
        this.jwtService = jwtService;
        this.redisService = redisService;
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

//        emailService.sendEmail(email, "Kích hoạt tài khoản", htmlContent, true, "HireHub Support");
        EmailMessage emailMessage = new EmailMessage.Builder().to(email).body(htmlContent).isHtml(true).type("activation").subject("Kích hoạt tài khoản").build();
        emailProducer.sendEmail(emailMessage);
    }
    public void sendPasswordResetEmail(String email) throws MessagingException {
        Context context = new Context();
        String token = TokenUtil.generateToken(32, true);

        tokenService.save(new Token(token, email, "reset-password"));

        String resetLink = frontendUrl + "/auth/reset-password?token=" + token + "&email=" + email;
        context.setVariable("resetLink", resetLink);
        String htmlContent = templateEngine.process("email/reset-password", context);

        EmailMessage emailMessage = new EmailMessage.Builder().to(email).body(htmlContent).isHtml(true).type("reset-password").subject("Thay đổi mật khẩu").build();
        emailProducer.sendEmail(emailMessage);
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
        if(role != null && role.getName().equals("user")) {

            this.sendActivationEmail(data.getEmail());
        }
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
    public User handleGoogleCallback(String token) {


        List<String> scopes = List.of("https://www.googleapis.com/auth/userinfo.email", "https://www.googleapis.com/auth/userinfo.profile");

        String scopeString = scopes.stream().collect(Collectors.joining(" "));
        String responseType = "code";
        Map<String, ?> data = Map.of("grant_type", "authorization_code","code", token, "redirect_uri", redirectUrl, "client_id", googleClientId, "client_secret", googleClientSecret);
        String url = "https://oauth2.googleapis.com/token";

        Map response = httpClientService.post(url, data, Map.class);

        String accessToken = (String) response.get("access_token");

        String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        Map result = httpClientService.get(userInfoUrl, headers, Map.class);

        String email = (String) result.get("email");

        String familyName = Optional.ofNullable((String)result.get("family_name")).orElse("");
        String givenName = Optional.ofNullable((String)result.get("given_name")).orElse("");
        String fullName = familyName + givenName;
        User user = userRepository.findByEmail(email);

        if(user != null)
            return user;

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setName(fullName);
        newUser.setIsVerified(true);
        newUser.setRole(roleService.getRoleByName("user").orElse(null));
        userService.save(newUser);
        return newUser;
    }
    public void clearToken(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String refreshTokenValue = null;
        String accessToken = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
             accessToken = Arrays.stream(cookies)
                    .filter(c -> Objects.equals(c.getName(), "jwt"))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);

            String username = jwtService.extractSubject(accessToken);

            refreshTokenValue = Arrays.stream(cookies)
                    .filter(c -> Objects.equals(c.getName(), "refresh_token"))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);

           var result = redisService.removeRefreshToken(username, refreshTokenValue);
        }

    }
}
