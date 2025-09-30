package org.example.hirehub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.example.hirehub.entity.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    @Query("SELECT j FROM Job j " +
            "JOIN j.recruiter r " +
            "WHERE (:title IS NULL OR j.title = :title) " +
            "AND (:company IS NULL OR r.name = :company) " +
            "AND (:location IS NULL OR r.address LIKE %:location%) " +
            "AND (:level IS NULL OR j.level = :level) " +
            "AND (:workspace IS NULL OR j.workspace = :workspace) " +
//            "AND (:postingDate IS NULL OR j.postingDate >= :postingDate) " +
            "AND (:keyword IS NULL OR (" +
            "       j.title LIKE %:keyword% " +
            "    OR r.name LIKE %:keyword% " +
            "    OR j.level LIKE %:keyword%" +
            ")) " +
            "ORDER BY j.postingDate DESC")
    List<Job> searchJobsDynamic(@Param("title") String title,
                                @Param("company") String company,
                                @Param("location") String location,
                                @Param("level") String level,
                                @Param("workspace") String workspace,
                                @Param("postingDate") LocalDateTime postingDate,
                                @Param("keyword") String keyword);





}

