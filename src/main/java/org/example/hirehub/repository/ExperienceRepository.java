package org.example.hirehub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.example.hirehub.entity.Experience;

import java.util.List;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    @Query(value = """
    SELECT e FROM Experience e
    JOIN FETCH e.user u
    WHERE u.id = ?1 AND e.isDeleted = false
""")
    public List<Experience> getExperienceByUserId(Long userId);

}
