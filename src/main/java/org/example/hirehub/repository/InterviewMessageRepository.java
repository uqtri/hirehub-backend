package org.example.hirehub.repository;

import org.example.hirehub.entity.InterviewMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewMessageRepository extends JpaRepository<InterviewMessage, Long> {
    
    @Query("SELECT m FROM InterviewMessage m " +
           "LEFT JOIN FETCH m.sender " +
           "LEFT JOIN FETCH m.room " +
           "WHERE m.room.id = :roomId " +
           "ORDER BY m.timestamp ASC")
    List<InterviewMessage> findByRoomIdOrderByTimestampAsc(@Param("roomId") Long roomId);
    
    @Query("SELECT m FROM InterviewMessage m " +
           "LEFT JOIN FETCH m.sender " +
           "LEFT JOIN FETCH m.room " +
           "WHERE m.room.id = :roomId AND m.type = :type " +
           "ORDER BY m.timestamp ASC")
    List<InterviewMessage> findByRoomIdAndTypeOrderByTimestampAsc(@Param("roomId") Long roomId, @Param("type") String type);
}

