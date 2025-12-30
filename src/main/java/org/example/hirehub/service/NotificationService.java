package org.example.hirehub.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.hirehub.dto.notification.CreateNotificationDTO;
import org.example.hirehub.dto.notification.NotificationSummaryDTO;
import org.example.hirehub.entity.Notification;
import org.example.hirehub.entity.User;
import org.example.hirehub.enums.NotificationType;
import org.example.hirehub.mapper.NotificationMapper;
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
    private final NotificationMapper notificationMapper;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository,
            SimpMessagingTemplate messagingTemplate, NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
        this.notificationMapper = notificationMapper;
    }

    public Notification createNotification(
            CreateNotificationDTO data) {
        User user = userRepository.findById(data.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(
                NotificationType.valueOf(data.getType()));
        notification.setTitle(data.getTitle());
        notification.setContent(data.getContent());
        notification.setRedirectUrl(data.getRedirectUrl());
        notification.setRead(false);
        notification.setIsDeleted(false);
        notification.setCreatedAt(LocalDateTime.now());

        // Save first to get ID
        Notification savedNotification = notificationRepository.save(notification);

        // Convert to DTO before sending via websocket to avoid lazy loading issues
        NotificationSummaryDTO notificationDTO = notificationMapper.toDTO(savedNotification);

        messagingTemplate.convertAndSendToUser(
                data.getUserId().toString(),
                "/queue/notifications",
                notificationDTO);

        return savedNotification;
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

    public void deleteNotificationByTypeAndContent(Long userId, String type, String content) {
        // Find and delete all matching notifications (in case there are duplicates)
        notificationRepository
                .findFirstByUserIdAndTypeAndContentAndIsDeletedFalse(userId, NotificationType.valueOf(type), content)
                .ifPresent(notification -> {
                    notification.setIsDeleted(true);
                    notificationRepository.save(notification);
                });
    }
}
