package org.example.hirehub.repository;

import org.example.hirehub.entity.Job;
import org.example.hirehub.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    @Query(value = """
            SELECT r FROM Resume r
            JOIN r.job j
            JOIN j.recruiter rc
            JOIN r.user u
            WHERE (:job IS NULL OR j.id = :job)
            AND (:user IS NULL OR u.id = :user)
            AND (:recruiter IS NULL OR rc.id = :recruiter)
            ORDER BY createdAt DESC
            """
    )
    List<Resume> searchResumesDynamic(@Param("user") Long user, @Param("job") Long job, @Param("recruiter") Long recruiter);

}
