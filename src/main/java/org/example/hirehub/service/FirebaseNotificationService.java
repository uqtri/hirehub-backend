package org.example.hirehub.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.example.hirehub.repository.FcmTokenRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

            firebaseMessaging.sendAsync(message);
        }
    }
}
