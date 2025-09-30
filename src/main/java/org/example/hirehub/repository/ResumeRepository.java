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
            SELECT r FROM RESUME r
            JOIN r.job z
            WHERE z.id = :id
            ORDER BY CreatedAt DESC;
            """
    )
    List<Resume> findResumesForCompany(@Param("id") Long id);

    @Query(value = """
            SELECT r FROM RESUME r
            JOIN r.user u
            WHERE u.id = :id
            ORDER BY CreatedAt DESC;
            """
    )
    List<Resume> findResumesForUser(@Param("id") Long id);

}
