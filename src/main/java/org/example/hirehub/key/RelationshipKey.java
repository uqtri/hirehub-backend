package org.example.hirehub.key;

import java.io.Serializable;
import java.util.Objects;

public class RelationshipKey implements Serializable {

    private Long userA;
    private Long userB;

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
