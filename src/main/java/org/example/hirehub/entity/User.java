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
    private String description;
    private Integer numberOfEmployees;

    private Integer foundedYear = 0;

    @OneToMany(mappedBy = "userA")
    private List<Relationship> relationshipsA;
    @OneToMany(mappedBy = "userB")
    private List<Relationship> relationshipsB;

    @OneToMany(mappedBy = "user")
    private List<Experience> experiences;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSkill> userSkills;

    private boolean isDeleted = false;


}
