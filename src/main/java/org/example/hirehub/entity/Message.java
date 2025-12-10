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
@Entity
@Setter
@Getter

public class Message {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String type;

    private String fileName;
    private String content;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    private LocalDateTime createdTime;
    private boolean isDeleted = false;

    private LocalDateTime createdAt = LocalDateTime.now();
    @OneToMany(mappedBy = "message")
    private List<UserMessage> seenBy;
}
