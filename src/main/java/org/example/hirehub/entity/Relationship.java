package org.example.hirehub.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.MapsId;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.example.hirehub.key.RelationshipKey;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Relationship {

    @EmbeddedId
    private RelationshipKey relationshipKey;

    @ManyToOne
    @MapsId("userA")
    private User userA;
    @MapsId("userB")
    @ManyToOne
    private User userB;
    private String status = "pending";

    private LocalDateTime createdAt = LocalDateTime.now();

    public Relationship(User userA, User userB) {
        this.userA = userA;
        this.userB = userB;
        this.relationshipKey = new RelationshipKey(userA.getId(), userB.getId());
    }
}
