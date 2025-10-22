package org.example.hirehub.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Token {
    @Id
    private String token;
    private String email;
    private String type;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Token(String token, String email, String type) {
        this.token = token;
        this.email = email;
        this.type = type;
    }

}
