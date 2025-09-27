package org.example.hirehub.key;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionKey  implements Serializable {
    private Long roleId;
    private Long permissionId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolePermissionKey that = (RolePermissionKey) o;
        return roleId.equals(that.roleId) && permissionId.equals(that.permissionId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(roleId, permissionId);
    }

}
