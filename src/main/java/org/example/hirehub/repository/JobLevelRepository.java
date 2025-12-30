package org.example.hirehub.repository;

import org.example.hirehub.entity.JobLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobLevelRepository extends JpaRepository<JobLevel, Long> {
    
    @Query("SELECT jl FROM JobLevel jl WHERE jl.isDeleted = false")
    List<JobLevel> findAllActive();
    
    Optional<JobLevel> findByLevel(String level);
    
    boolean existsByLevel(String level);
}

