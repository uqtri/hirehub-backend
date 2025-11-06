package org.example.hirehub.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.hirehub.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.example.hirehub.util.TokenUtil;
@Service
public class JwtService {

    @Value("${jwt.secret-key}")
    private String secretKey;

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken (CustomUserDetails user) {
        return Jwts.builder().setSubject(user.getUsername()).setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 )).signWith(getSignKey()).compact();
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignKey()).requireExpiration(null).build().parseClaimsJws(token).getBody();
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public Date extractExpiration (String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    public Boolean validateToken (String token) {
        try {
            final String username = extractClaim(token, Claims::getSubject);
            return !isTokenExpired(token);
        }
        catch (ExpiredJwtException e) {
            return false;
        }
        catch (Exception e) {
            return false;
        }
    }
    public String generateRefreshToken() {
        int TOKEN_LENGTH = 32;
        boolean alphaNumeric = false;
        String randomId = TokenUtil.generateToken(TOKEN_LENGTH, alphaNumeric);

        Long dayToMills = 1000 * 60 * 24L;
        Long monthToMills = dayToMills * 30;

        return Jwts.builder().setSubject(randomId).setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + monthToMills)).signWith(getSignKey()).compact();
    }
    public String extractSubject (String token) {

        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getSubject();
    }
}
