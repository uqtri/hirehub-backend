package org.example.hirehub.key;

import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@Setter
@Getter
public class RolePermissionKey  implements Serializable {
    private Long roleId;
    private Long permissionId;

    public RolePermissionKey(Long roleId, Long permissionId) {
        this.roleId = roleId;
        this.permissionId = permissionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolePermissionKey that = (RolePermissionKey) o;
        return Objects.equals(roleId, that.roleId) && Objects.equals(permissionId,that.permissionId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(roleId, permissionId);
    }

}
