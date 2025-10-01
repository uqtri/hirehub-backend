package org.example.hirehub.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Setter
@Getter

public class Message {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String message;
    @ManyToOne
    private User sender;

    @ManyToOne
    private User receiver;

    private LocalDateTime createdTime;
    private boolean isDeleted = false;
}
