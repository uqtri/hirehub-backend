package org.example.hirehub.repository;

import org.example.hirehub.entity.ConversationParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, Long> {

    @Query("""
        SELECT cp FROM ConversationParticipant cp
        WHERE cp.conversation.id = :conversationId
        AND cp.user.id = :userId
        AND cp.isDeleted = false
    """)
    Optional<ConversationParticipant> findByConversationIdAndUserId(
            @Param("conversationId") Long conversationId,
            @Param("userId") Long userId
    );

    @Query("""
        SELECT cp FROM ConversationParticipant cp
        WHERE cp.conversation.id = :conversationId
        AND cp.isDeleted = false
    """)
    List<ConversationParticipant> findAllByConversationId(@Param("conversationId") Long conversationId);

    @Query("""
        SELECT cp FROM ConversationParticipant cp
        WHERE cp.user.id = :userId
        AND cp.isDeleted = false
    """)
    List<ConversationParticipant> findAllByUserId(@Param("userId") Long userId);
}

