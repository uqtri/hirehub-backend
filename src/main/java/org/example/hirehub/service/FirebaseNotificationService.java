package org.example.hirehub.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.example.hirehub.repository.FcmTokenRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class FirebaseNotificationService {
    private final FcmTokenService fcmTokenService;
    private final FirebaseMessaging firebaseMessaging;

    FirebaseNotificationService(FcmTokenService fcmTokenService,  FirebaseMessaging firebaseMessaging) {
        this.fcmTokenService = fcmTokenService;
        this.firebaseMessaging = firebaseMessaging;
    }

    public void notifyUser(Long userId, String title, String body) {
        List<String> tokens = fcmTokenService.getTokensByUserId(userId);

        if (tokens.isEmpty()) {
            log.warn("⚠️ No FCM tokens for user {}", userId);
            return;
        }

        for (String token : tokens) {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(
                            Notification.builder()
                                    .setTitle(title)
                                    .setBody(body)
                                    .build()
                    )
                    .build();

            firebaseMessaging.sendAsync(message)
                    .addListener(() -> {
                        try {
                            log.info("✅ Notification sent | userId={} | token={}", userId, token);
                        } catch (Exception e) {
                            log.error("🔥 Failed to send notification | userId={} | token={}", userId, token, e);
                        }
                    }, Runnable::run);
        }
    }

}
