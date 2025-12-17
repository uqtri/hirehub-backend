package org.example.hirehub.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.hirehub.dto.notification.CreateNotificationDTO;
import org.example.hirehub.entity.Notification;
import org.example.hirehub.entity.User;
import org.example.hirehub.enums.NotificationType;
import org.example.hirehub.mapper.UserMapper;
import org.example.hirehub.repository.NotificationRepository;
import org.example.hirehub.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Setter
@Getter
@Service

public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserMapper userMapper;


    public NotificationService (NotificationRepository notificationRepository, UserRepository userRepository, SimpMessagingTemplate messagingTemplate, UserMapper userMapper){
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
        this.userMapper = userMapper;
    }

    public Notification createNotification(
            CreateNotificationDTO data
    ) {
        User user = userRepository.findById(data.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(
                NotificationType.valueOf(data.getType().toUpperCase())
        );
        notification.setTitle(data.getTitle());
        notification.setContent(data.getContent());
        notification.setRedirectUrl(data.getRedirectUrl());
        notification.setRead(false);
        notification.setIsDeleted(false);
        notification.setCreatedAt(LocalDateTime.now());

        messagingTemplate.convertAndSendToUser(
                data.getUserId().toString(),
                "/queue/notifications",
                userMapper.toDTO(user)

        );

        return notificationRepository.save(notification);
    }

    public Page<Notification> getMyNotifications(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return notificationRepository
                .findByUserAndIsDeletedFalseOrderByCreatedAtDesc(user, pageable);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countUnread(userId);
    }

    public void markAsRead(Long notificationId, Long userId) {
        Notification noti = getOwnedNotification(notificationId, userId);
        noti.setRead(true);
        notificationRepository.save(noti);
    }

    public void markAsUnread(Long notificationId, Long userId) {
        Notification noti = getOwnedNotification(notificationId, userId);
        noti.setRead(false);
        notificationRepository.save(noti);
    }

    public void deleteNotification(Long notificationId, Long userId) {
        Notification noti = getOwnedNotification(notificationId, userId);
        noti.setIsDeleted(true);
        notificationRepository.save(noti);
    }

    private Notification getOwnedNotification(Long notiId, Long userId) {
        Notification noti = notificationRepository.findById(notiId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!noti.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        return noti;
    }
}
