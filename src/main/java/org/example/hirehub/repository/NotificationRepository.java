package org.example.hirehub.repository;

import org.example.hirehub.entity.Notification;
import org.example.hirehub.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

        Page<Notification> findByUserAndIsDeletedFalseOrderByCreatedAtDesc(
                        User user,
                        Pageable pageable);

        @Query("""
                            SELECT COUNT(n)
                            FROM Notification n
                            WHERE n.user.id = :userId
                              AND n.isRead = false
                              AND n.isDeleted = false
                        """)
        long countUnread(Long userId);

        Optional<Notification> findFirstByUserIdAndTypeAndContentAndIsDeletedFalse(Long userId,
                        org.example.hirehub.enums.NotificationType type,
                        String content);
}
