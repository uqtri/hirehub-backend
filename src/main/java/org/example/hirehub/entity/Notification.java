package org.example.hirehub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hirehub.enums.NotificationType;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Người nhận notification
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Loại notification
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    // Tiêu đề ngắn (hiện trên toast)
    @Column(nullable = false)
    private String title;

    // Nội dung chi tiết
    @Column(columnDefinition = "TEXT")
    private String content;

    // Link click vào (job detail, request detail…)
    private String redirectUrl;

    // Đã đọc hay chưa (badge đỏ sống nhờ cái này)
    @Column(nullable = false)
    private boolean isRead = false;

    // Thời điểm tạo
    @Column(nullable = false)
    private LocalDateTime createdAt;

    private Boolean isDeleted;
}



