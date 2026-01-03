package org.example.hirehub.repository;

import org.example.hirehub.entity.InterviewRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewRoomRepository extends JpaRepository<InterviewRoom, Long> {
    Optional<InterviewRoom> findByRoomCode(String roomCode);
    
    List<InterviewRoom> findByRecruiterId(Long recruiterId);
    
    List<InterviewRoom> findByApplicantId(Long applicantId);
    
    List<InterviewRoom> findByJobId(Long jobId);
    
    boolean existsByRoomCode(String roomCode);
}

