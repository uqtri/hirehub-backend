package org.example.hirehub.repository;

import org.example.hirehub.entity.UserMessage;
import org.example.hirehub.key.UserMessageKey;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.example.hirehub.entity.Message;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMessageRepository extends JpaRepository<UserMessage, UserMessageKey> {

    @Query("""
        SELECT um FROM UserMessage um
        WHERE um.user.id = :userId AND um.message.id = :messageId
    """)
    Optional<UserMessage> findUsersSeen(Long userId, Long messageId);
}
