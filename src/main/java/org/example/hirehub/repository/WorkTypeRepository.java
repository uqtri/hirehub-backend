package org.example.hirehub.repository;

import org.example.hirehub.entity.WorkType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkTypeRepository extends JpaRepository<WorkType, Long> {
    
    @Query("SELECT wt FROM WorkType wt WHERE wt.isDeleted = false")
    List<WorkType> findAllActive();
    
    Optional<WorkType> findByWorkspace(String workspace);
    
    boolean existsByWorkspace(String workspace);
}





