package org.example.hirehub.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.example.hirehub.entity.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
        // User query - excludes banned and deleted jobs
        @Query("SELECT j FROM Job j " +
                        "JOIN j.recruiter r " +
                        "WHERE j.isDeleted = false " +
                        "AND (j.is_banned IS NULL OR j.is_banned = false) " +
                        "AND (:title IS NULL OR j.title = :title) " +
                        "AND (:company IS NULL OR r.name = :company) " +
                        "AND (:location IS NULL OR r.address LIKE %:location%) " +
                        "AND (:level IS NULL OR j.level = :level) " +
                        "AND (:workspace IS NULL OR j.workspace = :workspace) " +
                        "AND (:province IS NULL OR j.address LIKE %:province%) " +
                        // "AND (:postingDate IS NULL OR j.postingDate >= :postingDate) " +
                        "AND (:keyword IS NULL OR (" +
                        "       j.title LIKE %:keyword% " +
                        "    OR r.name LIKE %:keyword% " +
                        "    OR j.level LIKE %:keyword%" +
                        ")) " +
                        "ORDER BY j.postingDate DESC")
        Page<Job> searchJobsDynamic(@Param("title") String title,
                        @Param("company") String company,
                        @Param("location") String location,
                        @Param("level") String level,
                        @Param("workspace") String workspace,
                        @Param("postingDate") LocalDateTime postingDate,
                        @Param("keyword") String keyword,
                        @Param("province") String province,
                        Pageable pageable);

        // Admin query - includes ALL jobs (including banned)
        @Query("SELECT j FROM Job j " +
                        "JOIN j.recruiter r " +
                        "WHERE j.isDeleted = false " +
                        "AND (:keyword IS NULL OR (" +
                        "       j.title LIKE %:keyword% " +
                        "    OR r.name LIKE %:keyword% " +
                        ")) " +
                        "ORDER BY j.postingDate DESC")
        Page<Job> searchJobsAdmin(@Param("keyword") String keyword, Pageable pageable);

        Page<Job> findByRecruiterIdAndIsDeletedFalse(Long recruiterId, Pageable pageable);

        @Query("""
                        SELECT j FROM Job j
                        WHERE j.recruiter.id = :recruiterId
                        AND (:status IS NULL OR j.status = :status)
                        AND (:keyword IS NULL OR j.title LIKE %:keyword%)
                        ORDER BY j.postingDate DESC
                        """)
        Page<Job> findByRecruiterWithFilters(
                        @Param("recruiterId") Long recruiterId,
                        @Param("status") String status,
                        @Param("keyword") String keyword,
                        Pageable pageable);

        List<Job> findAllByIsDeletedFalse();

        @Query("""
                        SELECT DISTINCT j FROM Job j
                        LEFT JOIN FETCH j.skills js
                        LEFT JOIN FETCH js.skill
                        LEFT JOIN FETCH j.recruiter
                        WHERE j.isDeleted = false
                        """)
        List<Job> findAllByIsDeletedFalseWithDetails();

        @Query("""
                        SELECT j FROM Job j
                        LEFT JOIN FETCH j.skills js
                        LEFT JOIN FETCH js.skill
                        LEFT JOIN FETCH j.recruiter
                        WHERE j.id = :id
                        """)
        java.util.Optional<Job> findByIdWithDetails(@Param("id") Long id);
}
