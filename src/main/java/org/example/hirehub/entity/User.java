package org.example.hirehub.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Table(name = "\"user\"")
@Entity
@Getter
@Setter

public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String email;
    private String name;
    private String password;
    private String address;
    @ManyToOne
    private Role role;
    private String avatar;
    private Boolean isVerified = false;
    private Boolean isBanned = false;

    @Column(columnDefinition = "TEXT")
    private String description;
    private String numberOfEmployees;
    private Integer foundedYear = 0;
    private String github;
    private String resume_link;
    private String field;

    @ManyToMany
    private List<LanguageLevel> languages;

    @OneToMany(mappedBy = "userA")
    private List<Relationship> relationshipsA;
    @OneToMany(mappedBy = "userB")
    private List<Relationship> relationshipsB;

    @OneToMany(mappedBy = "user")
    private List<Experience> experiences;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSkill> userSkills;

    private boolean isDeleted = false;

    @OneToMany (mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Study> studies;

    private LocalDateTime createdAt = LocalDateTime.now();
}
