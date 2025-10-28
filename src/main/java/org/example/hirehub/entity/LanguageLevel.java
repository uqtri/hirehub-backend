package org.example.hirehub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
public class LanguageLevel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String level;

    @ManyToOne
    private Language language;

    @ManyToMany
    private List<User> users;

    private LocalDateTime createdAt = LocalDateTime.now();

}
