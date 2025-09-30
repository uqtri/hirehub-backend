package org.example.hirehub.key;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.Objects;
@AllArgsConstructor
@Embeddable
public class JobSkillKey implements Serializable {
     private Long jobId;
     private Long skillId;

     @Override
     public boolean equals(Object o) {
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;
         JobSkillKey that = (JobSkillKey) o;

         return jobId.equals(that.jobId) && skillId.equals(that.skillId);
     }
     @Override
    public int hashCode() {
        return Objects.hash(jobId, skillId);
     }
}
