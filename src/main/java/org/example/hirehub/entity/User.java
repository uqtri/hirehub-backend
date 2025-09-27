package org.example.hirehub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "\"user\"")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String address;
    @OneToOne
    private Role role;
    private String avatar;
    private boolean isVerified = false;
    private boolean isBanned = false;
    private String description;
    private int numberOfEmployees;

    private int foundedYear = 0;

    @OneToMany(mappedBy = "userA")
    private List<Relationship> relationshipsA;
    @OneToMany(mappedBy = "userB")
    private List<Relationship> relationshipsB;

    @OneToMany(mappedBy = "user")
    private List<Experience> experiences;

    private boolean isDeleted = false;


}
