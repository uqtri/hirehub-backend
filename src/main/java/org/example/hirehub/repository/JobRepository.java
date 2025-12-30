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
        // User query - only shows APPROVED jobs (excludes PENDING, DRAFT, BANNED,
        // CLOSED)
        @Query("SELECT j FROM Job j " +
                        "JOIN j.recruiter r " +
                        "WHERE j.isDeleted = false " +
                        "AND j.status = 'APPROVED' " +
                        "AND (j.is_banned IS NULL OR j.is_banned = false) " +
                        "AND (:title IS NULL OR j.title = :title) " +
                        "AND (:company IS NULL OR r.name = :company) " +
                        "AND (:location IS NULL OR r.address ILIKE %:location%) " +
                        "AND ((:levels) IS NULL OR LOWER(CAST(j.level AS string)) IN (:levels)) " +
                        "AND ((:workspaces) IS NULL OR LOWER(CAST(j.workspace AS string)) IN (:workspaces)) " +
                        "AND ((:types) IS NULL OR LOWER(CAST(j.type AS string)) IN (:types)) " +
                        "AND ((:fields) IS NULL OR LOWER(CAST(r.field AS string)) IN (:fields)) " +
                        "AND (:province IS NULL OR j.address ILIKE %:province%) " +
                        // "AND (:postingDate IS NULL OR j.postingDate >= :postingDate) " +
                        "AND (:keyword IS NULL OR (" +
                        "       j.title ILIKE %:keyword% " +
                        "    OR r.name ILIKE %:keyword% " +
                        "    OR j.level ILIKE %:keyword%" +
                        ")) " +
                        "ORDER BY j.postingDate DESC")
        Page<Job> searchJobsDynamic(@Param("title") String title,
                        @Param("company") String company,
                        @Param("location") String location,
                        @Param("levels") List<String> levels,
                        @Param("workspaces") List<String> workspaces,
                        @Param("types") List<String> types,
                        @Param("fields") List<String> fields,
                        @Param("postingDate") LocalDateTime postingDate,
                        @Param("keyword") String keyword,
                        @Param("province") String province,
                        Pageable pageable);

        // Admin query - includes ALL jobs except DRAFT (PENDING, APPROVED, BANNED)
        @Query("SELECT j FROM Job j " +
                        "JOIN j.recruiter r " +
                        "WHERE j.isDeleted = false " +
                        "AND j.status != 'DRAFT' " +
                        "AND (:keyword IS NULL OR (" +
                        "       j.title ILIKE %:keyword% " +
                        "    OR r.name ILIKE %:keyword% " +
                        ")) " +
                        "AND (:level IS NULL OR j.level = :level) " +
                        "AND (:recruiter IS NULL OR r.name ILIKE %:recruiter%) " +
                        "AND (:status IS NULL " +
                        "   OR (:status = 'pending' AND j.status = 'PENDING') " +
                        "   OR (:status = 'approved' AND j.status = 'APPROVED') " +
                        "   OR (:status = 'banned' AND j.status = 'BANNED')) " +
                        "ORDER BY j.postingDate DESC")
        Page<Job> searchJobsAdmin(@Param("keyword") String keyword,
                        @Param("level") String level,
                        @Param("status") String status,
                        @Param("recruiter") String recruiter,
                        Pageable pageable);

        Page<Job> findByRecruiterIdAndIsDeletedFalse(Long recruiterId, Pageable pageable);

        @Query("""
                        SELECT j FROM Job j
                        WHERE j.recruiter.id = :recruiterId
                        AND (:status IS NULL OR j.status = :status)
                        AND (:keyword IS NULL OR j.title ILIKE %:keyword%)
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
