package org.example.hirehub.repository;

import org.example.hirehub.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("""
        SELECT DISTINCT c FROM Conversation c
        JOIN c.participants p
        WHERE p.user.id = :userId
        AND p.isDeleted = false
        ORDER BY c.updatedAt DESC
    """)
    List<Conversation> findByUserId(@Param("userId") Long userId);

    @Query("""
        SELECT c FROM Conversation c
        JOIN c.participants p1
        JOIN c.participants p2
        WHERE c.type = 'DIRECT'
        AND p1.user.id = :user1Id
        AND p2.user.id = :user2Id
        AND p1.isDeleted = false
        AND p2.isDeleted = false
        AND SIZE(c.participants) = 2
    """)
    Optional<Conversation> findDirectConversationBetween(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    @Query("""
        SELECT c FROM Conversation c
        JOIN FETCH c.participants p
        WHERE c.id = :conversationId
    """)
    Optional<Conversation> findByIdWithParticipants(@Param("conversationId") Long conversationId);
}

