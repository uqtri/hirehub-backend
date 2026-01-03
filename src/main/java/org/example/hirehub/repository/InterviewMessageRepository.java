package org.example.hirehub.repository;

import org.example.hirehub.entity.InterviewMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewMessageRepository extends JpaRepository<InterviewMessage, Long> {
    List<InterviewMessage> findByRoomIdOrderByTimestampAsc(Long roomId);
    
    List<InterviewMessage> findByRoomIdAndTypeOrderByTimestampAsc(Long roomId, String type);
}

