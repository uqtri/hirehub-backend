package org.example.hirehub.key;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Embeddable

public class ExperienceSkillKey implements Serializable {
    private  Long experienceId;
    private  Long skillId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExperienceSkillKey that = (ExperienceSkillKey) o;
        return experienceId.equals(that.experienceId) && skillId.equals(that.skillId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(experienceId, skillId);
    }
}
