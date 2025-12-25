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

    // Tìm participant còn active (chưa rời và chưa bị xóa)
    @Query("""
        SELECT cp FROM ConversationParticipant cp
        WHERE cp.conversation.id = :conversationId
        AND cp.user.id = :userId
        AND cp.isDeleted = false
        AND cp.leavedAt IS NULL
    """)
    Optional<ConversationParticipant> findByConversationIdAndUserId(
            @Param("conversationId") Long conversationId,
            @Param("userId") Long userId
    );

    // Tìm participant bao gồm cả những người đã rời (để có thể mời lại)
    @Query("""
        SELECT cp FROM ConversationParticipant cp
        WHERE cp.conversation.id = :conversationId
        AND cp.user.id = :userId
        AND cp.isDeleted = false
    """)
    Optional<ConversationParticipant> findByConversationIdAndUserIdIncludeLeaved(
            @Param("conversationId") Long conversationId,
            @Param("userId") Long userId
    );

    // Lấy tất cả participant còn active
    @Query("""
        SELECT cp FROM ConversationParticipant cp
        WHERE cp.conversation.id = :conversationId
        AND cp.isDeleted = false
        AND cp.leavedAt IS NULL
    """)
    List<ConversationParticipant> findAllByConversationId(@Param("conversationId") Long conversationId);

    // Lấy tất cả participant bao gồm cả những người đã rời
    @Query("""
        SELECT cp FROM ConversationParticipant cp
        WHERE cp.conversation.id = :conversationId
        AND cp.isDeleted = false
    """)
    List<ConversationParticipant> findAllByConversationIdIncludeLeaved(@Param("conversationId") Long conversationId);

    @Query("""
        SELECT cp FROM ConversationParticipant cp
        WHERE cp.user.id = :userId
        AND cp.isDeleted = false
        AND cp.leavedAt IS NULL
    """)
    List<ConversationParticipant> findAllByUserId(@Param("userId") Long userId);
}

