package org.example.hirehub.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.example.hirehub.dto.auth.LoginRequest;
import org.example.hirehub.security.CustomUserDetails;
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
    public AuthController(JwtService jwtService, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
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
}
