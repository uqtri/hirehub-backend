package org.example.hirehub.key;

import lombok.NoArgsConstructor;
import org.example.hirehub.entity.User;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
public class RelationshipKey implements Serializable {

    private Long userA;
    private Long userB;

    public RelationshipKey(Long userA, Long userB) {
        this.userA = userA;
        this.userB = userB;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelationshipKey that = (RelationshipKey) o;

        return (Objects.equals(userA, that.userA) && Objects.equals(userB, that.userB)) ||
                (Objects.equals(userA, that.userB) && Objects.equals(userB, that.userA));
    }

    @Override
    public int hashCode() {
        return Objects.hash(userA) +  Objects.hash(userB);
    }

}
