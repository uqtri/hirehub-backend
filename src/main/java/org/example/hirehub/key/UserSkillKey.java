package org.example.hirehub.key;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Setter
@Getter

public class UserSkillKey implements Serializable {
    private Long userId;
    private Long skillId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSkillKey that = (UserSkillKey) o;
        return userId.equals(that.userId) && skillId.equals(that.skillId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(userId, skillId);
    }
}
