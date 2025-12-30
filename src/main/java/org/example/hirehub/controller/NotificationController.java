package org.example.hirehub.controller;

import org.example.hirehub.dto.notification.CreateNotificationDTO;
import org.example.hirehub.dto.notification.NotificationSummaryDTO;
import org.example.hirehub.entity.Notification;
import org.example.hirehub.mapper.NotificationMapper;
import org.example.hirehub.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    public NotificationController(NotificationService notificationService, NotificationMapper notificationMapper) {
        this.notificationService = notificationService;
        this.notificationMapper = notificationMapper;
    }

    @GetMapping
    public Page<NotificationSummaryDTO> getMyNotifications(
            @RequestParam("userId") Long userId,
            Pageable pageable) {
        Page<Notification> notifications = notificationService.getMyNotifications(userId, pageable);
        return notifications.map(notificationMapper::toDTO);
    }

    @GetMapping("/unread-count")
    public long getUnreadCount(
            @RequestParam("userId") Long userId) {
        return notificationService.getUnreadCount(userId);
    }

    @PutMapping("/{id}/read")
    public void markAsRead(
            @PathVariable Long id,
            @RequestParam("userId") Long userId) {
        notificationService.markAsRead(id, userId);
    }

    @PutMapping("/{id}/unread")
    public void markAsUnread(
            @PathVariable Long id,
            @RequestParam("userId") Long userId) {
        notificationService.markAsUnread(id, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteNotification(
            @PathVariable Long id,
            @RequestParam("userId") Long userId) {
        notificationService.deleteNotification(id, userId);
    }

    @PutMapping("/mark-all-read")
    public void markAllAsRead(@RequestParam("userId") Long userId) {
        notificationService.markAllAsRead(userId);
    }

    @PostMapping("")
    public NotificationSummaryDTO createNotification(@Payload CreateNotificationDTO data) throws IOException {
        Notification notification = notificationService.createNotification(data);
        return notificationMapper.toDTO(notification);
    }
}
