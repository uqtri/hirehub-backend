package org.example.hirehub.repository;

import org.example.hirehub.entity.InterviewScheduleRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewScheduleRequestRepository extends JpaRepository<InterviewScheduleRequest, Long> {
    Optional<InterviewScheduleRequest> findByRequestCode(String requestCode);

    List<InterviewScheduleRequest> findByApplicantIdAndStatus(Long applicantId, String status);

    List<InterviewScheduleRequest> findByRecruiterIdAndStatus(Long recruiterId, String status);

    List<InterviewScheduleRequest> findByApplicantIdOrderByCreatedAtDesc(Long applicantId);

    List<InterviewScheduleRequest> findByRecruiterIdOrderByCreatedAtDesc(Long recruiterId);

    boolean existsByRequestCode(String requestCode);
}
