package org.example.hirehub.service;

import lombok.extern.slf4j.Slf4j;
import org.example.hirehub.entity.FcmToken;
import org.example.hirehub.entity.User;
import org.example.hirehub.repository.FcmTokenRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;
    private final UserService userService;
    FcmTokenService(FcmTokenRepository fcmTokenRepository, UserService userService) {
        this.fcmTokenRepository = fcmTokenRepository;
        this.userService = userService;
    }

    public List<String> getTokensByUserId(Long userId) {
        return fcmTokenRepository.findByUserId(userId)
                .stream()
                .map(FcmToken::getToken)
                .toList();
    }

    public void saveToken(Long userId, String token) {

        if (fcmTokenRepository.existsByUserIdAndToken(userId, token)) {
            log.info("🔁 FCM token already exists | userId={}", userId);
            return;
        }

        User user = userService.getUserById(userId);

        FcmToken fcmToken = new FcmToken();
        fcmToken.setUser(user);
        fcmToken.setToken(token);

        fcmTokenRepository.save(fcmToken);

        log.info("✅ Saved FCM token | userId={}", userId);
    }
}
