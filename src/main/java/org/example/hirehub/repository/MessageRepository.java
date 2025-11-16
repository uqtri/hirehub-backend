package org.example.hirehub.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.example.hirehub.entity.Message;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m " +
            "JOIN m.sender s " +
            "JOIN m.receiver r " +
            "WHERE (r.id = ?1 AND s.id = ?2) " +
            "   OR (s.id = ?1 AND r.id = ?2)")
    List<Message> findConversation(Long userA, Long userB, Sort sort);

    @Query("""
            SELECT\s
                  CASE
                      WHEN m.sender.id < m.receiver.id THEN m.sender.id
                      ELSE m.receiver.id
                  END AS userA,
                  CASE
                      WHEN m.sender.id < m.receiver.id THEN m.receiver.id
                      ELSE m.sender.id
                  END AS userB,
                  MAX(m.createdAt)
              FROM Message m
              WHERE m.sender.id = :userId OR m.receiver.id = :userId
              GROUP BY userA, userB
            """)
    List<Object[]> getLatestTimestamps(Long userId);

    @Query("""
    SELECT m
    FROM Message m
    WHERE\s
        (
            (m.sender.id = :userA AND m.receiver.id = :userB)
            OR
            (m.sender.id = :userB AND m.receiver.id = :userA)
        )
        AND m.createdAt = (
            SELECT MAX(m2.createdAt)
            FROM Message m2
            WHERE\s
                (
                    (m2.sender.id = :userA AND m2.receiver.id = :userB)
                    OR
                    (m2.sender.id = :userB AND m2.receiver.id = :userA)
                )
        )
""")
    Message getLatestMessageBetween(Long userA, Long userB);



}
