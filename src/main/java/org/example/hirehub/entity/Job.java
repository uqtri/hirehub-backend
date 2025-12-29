package org.example.hirehub.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Setter
@Getter

public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String apply_link;
    @ManyToOne
    private User recruiter;
    private String level;

    private boolean is_banned = false;
    private String workspace;
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobSkill> skills = new ArrayList<>();

    private String address;

    private boolean isDeleted = false;
    private Long hit_counter = 0L;
    private LocalDateTime postingDate = LocalDateTime.now();
    private String type;
    private String status = "PENDING"; // PENDING, APPROVED, BANNED, CLOSED, DRAFT
    private LocalDateTime createdAt = LocalDateTime.now();

    // AI violation check results
    private String violationType;
    @Column(columnDefinition = "TEXT")
    private String violationExplanation;

    // Admin ban reason
    @Column(columnDefinition = "TEXT")
    private String banReason;

    // Explicit getter/setter for is_banned to fix naming convention issues
    public boolean getIs_banned() {
        return is_banned;
    }

    public void setIs_banned(boolean is_banned) {
        this.is_banned = is_banned;
    }
}
