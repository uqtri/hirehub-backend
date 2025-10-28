package org.example.hirehub.repository;

import org.example.hirehub.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {

    @Query("""
    SELECT s FROM Study s 
    WHERE s.user.id = :userId
""")
    List<Study> findStudiesByUserId(Long userId);
}
