package org.example.hirehub.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.MapsId;

import lombok.Getter;
import lombok.Setter;

import org.example.hirehub.key.RelationshipKey;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter

public class Relationship {

    @EmbeddedId
    private RelationshipKey relationshipKey;

    @ManyToOne
    @MapsId("userA")
    private User userA;
    @MapsId("userB")
    @ManyToOne
    private User userB;
    private String status;

    private LocalDateTime createdAt = LocalDateTime.now();

}
