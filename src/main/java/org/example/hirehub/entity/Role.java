package org.example.hirehub.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter

public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RolePermission> rolePermission;

    private boolean isDeleted = false;

}
