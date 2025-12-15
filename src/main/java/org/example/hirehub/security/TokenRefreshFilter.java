//package org.example.hirehub.security;
//
//import io.jsonwebtoken.Claims;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.example.hirehub.service.JwtService;
//import org.example.hirehub.service.RedisService;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.Objects;
//
//@Configuration
//public class TokenRefreshFilter extends OncePerRequestFilter {
//
//
//    private final JwtService jwtService;
//    private final RedisService redisService;
//    private final CustomUserdetailService customUserdetailService;
//
//    public TokenRefreshFilter(JwtService jwtService, RedisService redisService, CustomUserdetailService customUserdetailService) {
//        this.jwtService = jwtService;
//        this.redisService = redisService;
//        this.customUserdetailService = customUserdetailService;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
//        Cookie[] cookies = request.getCookies();
//        String accessToken = null;
//        String refreshToken = null;
//
//        if (cookies != null) {
//            accessToken = Arrays.stream(cookies).filter(cookie -> Objects.equals(cookie.getName(), "jwt")).map(Cookie::getValue).findFirst().orElse(null);
////            refreshToken = Arrays.stream(cookies).filter(cookie -> Objects.equals(cookie.getName(), "refresh_token")).map(Cookie::getValue).findFirst().orElse(null);
//        }
//
////        if(accessToken != null && !accessToken.isEmpty() && refreshToken != null && !refreshToken.isEmpty() && !jwtService.validateToken(accessToken)) {
////            String email = jwtService.extractSubject(accessToken);
//
////            if(email != null && redisService.isExistRefreshToken(email, refreshToken) && jwtService.validateToken(refreshToken)) {
////                CustomUserDetails user = customUserdetailService.loadUserByUsername(jwtService.extractClaim(accessToken, Claims::getSubject));
////
////                String newAccessToken = jwtService.generateToken(user);
////                Cookie newCookie = new Cookie("jwt", newAccessToken);
////                newCookie.setPath("/");
////                newCookie.setHttpOnly(true);
////                response.addCookie(newCookie);
//
////                String newRefreshToken = jwtService.generateRefreshToken();
////                Cookie newRefreshCookie = new Cookie("refresh_token", newRefreshToken);
////                newRefreshCookie.setPath("/");
////                response.addCookie(newRefreshCookie);
////                redisService.removeRefreshToken(email, refreshToken);
////                redisService.addRefreshToken(email, newRefreshToken);
////            }
////        }
//        filterChain.doFilter(request, response);
//    }
//}
