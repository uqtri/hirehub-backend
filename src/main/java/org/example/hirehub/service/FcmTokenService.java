package org.example.hirehub.service;

import org.example.hirehub.entity.FcmToken;
import org.example.hirehub.repository.FcmTokenRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;
    FcmTokenService(FcmTokenRepository fcmTokenRepository) {
        this.fcmTokenRepository = fcmTokenRepository;
    }

    public List<String> getTokensByUserId(Long userId) {
        return fcmTokenRepository.findByUserId(userId)
                .stream()
                .map(FcmToken::getToken)
                .toList();
    }
}
