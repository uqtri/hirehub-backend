package org.example.hirehub.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.hirehub.service.JwtService;
import org.example.hirehub.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private UserService userService;
    private final JwtService jwtService;
    private final CustomUserdetailService customUserdetailService;
    JwtAuthenticationFilter(final JwtService jwtService, CustomUserdetailService customUserdetailService) {
        this.jwtService = jwtService;
        this.customUserdetailService = customUserdetailService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

       Cookie[] cookies = request.getCookies();
       String token = null;
        if (cookies != null) {

            token = Arrays.stream(cookies).filter(cookie -> Objects.equals(cookie.getName(), "jwt")).map(Cookie::getValue).findFirst().orElse(null);
        }
        if(token != null && jwtService.validateToken(token)) {
            CustomUserDetails user = customUserdetailService.loadUserByUsername(jwtService.extractClaim(token, Claims::getSubject));
            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}
