package org.example.hirehub.key;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;


import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Embeddable

public class UserMessageKey implements Serializable {
    private  Long userId;
    private  Long messageId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserMessageKey that = (UserMessageKey) o;
        return userId.equals(that.userId) && messageId.equals(that.messageId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(userId, messageId);
    }
}
