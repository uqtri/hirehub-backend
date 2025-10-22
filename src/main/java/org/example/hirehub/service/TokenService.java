package org.example.hirehub.service;

import jakarta.persistence.Entity;
import org.example.hirehub.entity.Token;
import org.example.hirehub.repository.TokenRepository;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public Token save (Token token) {
        return tokenRepository.save(token);
    }
    public void delete (Token token) {
        tokenRepository.delete(token);
    }
    public Token findTokenByIdAndType(String id, String type) {
        return tokenRepository.findById(id).filter(t -> t.getType().equals(type)).orElse(null);
    }
}
