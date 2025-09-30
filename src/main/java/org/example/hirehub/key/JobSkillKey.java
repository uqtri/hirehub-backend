package org.example.hirehub.key;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobSkillKey implements Serializable {
     private Long jobId;
     private Long skillId;

     @Override
     public boolean equals(Object o) {
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;
         JobSkillKey that = (JobSkillKey) o;

         return Objects.equals(jobId, that.jobId) && Objects.equals(skillId, that.skillId);
     }
     @Override
    public int hashCode() {
        return Objects.hash(jobId, skillId);
     }
}
