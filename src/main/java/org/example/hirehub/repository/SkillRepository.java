package org.example.hirehub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.example.hirehub.entity.Skill;

@Repository
public interface SkillRepository  extends JpaRepository<Skill, Long> {

    @Query(value = """
    SELECT DISTINCT sk FROM Skill sk
    WHERE sk.id IN :ids
    
""")
    List<Skill> findSkillsByIds(List<Long> ids);

}
