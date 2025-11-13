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

}
