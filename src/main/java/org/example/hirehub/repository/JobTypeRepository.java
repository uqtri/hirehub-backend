package org.example.hirehub.repository;

import org.example.hirehub.entity.JobType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobTypeRepository extends JpaRepository<JobType, Long> {
    
    @Query("SELECT jt FROM JobType jt WHERE jt.isDeleted = false")
    List<JobType> findAllActive();
    
    Optional<JobType> findByType(String type);
    
    boolean existsByType(String type);
}




