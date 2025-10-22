package org.example.hirehub.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.example.hirehub.dto.auth.LoginRequest;
import org.example.hirehub.security.CustomUserDetails;
import org.example.hirehub.service.AuthService;
import org.example.hirehub.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuthService authService;

    public AuthController(JwtService jwtService, AuthenticationManager authenticationManager, AuthService authService) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        Authentication authentication = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        Authentication result = authenticationManager.authenticate(authentication);

        if (!result.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Email hoặc mật khẩu không đúng"));
        }
        Cookie cookie = new Cookie("jwt", jwtService.generateToken((CustomUserDetails)result.getPrincipal()));

        response.addCookie(cookie);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Đăng nhập thành công"));
    }
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {

        Cookie cookie = new Cookie("jwt", "");
        response.addCookie(cookie);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Đăng xuất thành công"));
    }
    @PostMapping("/send-activation")
    public ResponseEntity<Map<String, String>> sendActivation(@RequestParam("email") String email) throws Exception {

        authService.sendActivationEmail(email);
        return ResponseEntity.ok().body(Map.of("message", "Gửi mail xác nhận thành công"));
    }
    @PostMapping("/activate")
    public ResponseEntity<Map<String, String>> activate(@RequestParam("token") String token, @RequestParam("email") String email) throws Exception {

        authService.activate(token, email);
        return ResponseEntity.ok().body(Map.of("message", "Kích hoạt tài khoản thành công"));
    }
    @PostMapping("/send-password-reset")
    public ResponseEntity<Map<String, String>> sendPasswordReset(@RequestParam("email") String email) throws Exception {

        authService.sendPasswordResetEmail(email);
        return ResponseEntity.ok().body(Map.of("message", "Gửi mail đổi mật khẩu thành công"));
    }
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestParam("token") String token, @RequestParam("email") String email, @RequestBody Map<String, String> body) throws Exception {

        String newPassword = body.get("newPassword");

        authService.resetPassword(token, email, newPassword);
        return ResponseEntity.ok().body(Map.of("message", "Thay đổi mật khẩu thành công"));
    }
}

