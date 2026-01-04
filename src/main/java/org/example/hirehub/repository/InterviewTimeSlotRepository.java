package org.example.hirehub.repository;

import org.example.hirehub.entity.InterviewTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewTimeSlotRepository extends JpaRepository<InterviewTimeSlot, Long> {
    List<InterviewTimeSlot> findByScheduleRequestIdOrderByProposedTimeAsc(Long scheduleRequestId);

    List<InterviewTimeSlot> findByScheduleRequestIdAndIsAvailableTrue(Long scheduleRequestId);
}
