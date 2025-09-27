package org.example.hirehub.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.hirehub.key.RolePermissionKey;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolePermission {

    @EmbeddedId
    private RolePermissionKey rolePermissionKey;

    @ManyToOne
    @MapsId("roleId")
    private Role role;
    @ManyToOne
    @MapsId("permissionId")
    private Permission permission;
}
