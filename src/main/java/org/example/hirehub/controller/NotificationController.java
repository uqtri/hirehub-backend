package org.example.hirehub.controller;

import org.example.hirehub.dto.message.CreateMessageDTO;
import org.example.hirehub.dto.notification.CreateNotificationDTO;
import org.example.hirehub.entity.Notification;
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

    public  NotificationController (NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public Page<Notification> getMyNotifications(
            @RequestParam("userId") Long userId,
            Pageable pageable
    ) {
        return notificationService.getMyNotifications(userId, pageable);
    }

    @GetMapping("/unread-count")
    public long getUnreadCount(
            @RequestAttribute("userId") Long userId
    ) {
        return notificationService.getUnreadCount(userId);
    }

    @PutMapping("/{id}/read")
    public void markAsRead(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId
    ) {
        notificationService.markAsRead(id, userId);
    }

    @PutMapping("/{id}/unread")
    public void markAsUnread(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId
    ) {
        notificationService.markAsUnread(id, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteNotification(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId
    ) {
        notificationService.deleteNotification(id, userId);
    }

    @PostMapping("")
    public void createNotification(@Payload CreateNotificationDTO data) throws IOException {
        notificationService.createNotification(data);

    }
}
