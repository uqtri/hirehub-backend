package org.example.hirehub.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.example.hirehub.entity.Message;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE (m.receiver_id = ?1 AND m.sender_id = ?2) OR (m.sender_id = ?2 AND m.receiver_id = ?1)")
    List<Message> findConservation(Long userA, Long userB, Sort sort);
}
