package org.example.hirehub.repository;

import org.example.hirehub.entity.UserMessage;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.example.hirehub.entity.Message;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    @Query("""
        SELECT DISTINCT m FROM Message m
        LEFT JOIN FETCH m.seenBy
        WHERE m.conversation.id = :conversationId
        AND m.isDeleted = false
        ORDER BY m.createdAt ASC
    """)
    List<Message> findByConversationId(@Param("conversationId") Long conversationId, Sort sort);

    @Query("""
        SELECT DISTINCT m FROM Message m
        LEFT JOIN FETCH m.seenBy
        WHERE m.conversation.id = :conversationId
        AND m.isDeleted = false
        AND (:messageTypes IS NULL OR m.type IN :messageTypes)
        ORDER BY m.createdAt ASC
    """)
    List<Message> findByConversationId(@Param("conversationId") Long conversationId, 
                                       @Param("messageTypes") List<String> messageTypes, 
                                       Sort sort);

    @Query("""
        SELECT m FROM Message m
        WHERE m.conversation.id = :conversationId
        AND m.isDeleted = false
        ORDER BY m.createdAt DESC
    """)
    List<Message> findLatestByConversationId(@Param("conversationId") Long conversationId, Sort sort);

    @Query("""
        SELECT um FROM UserMessage um
        WHERE um.user.id = :userId AND um.message.id = :messageId
    """)
    Optional<UserMessage> findRecord(@Param("userId") Long userId, @Param("messageId") Long messageId);
}
