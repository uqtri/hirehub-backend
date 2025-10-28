package org.example.hirehub.repository;

import org.example.hirehub.entity.LanguageLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LanguageLevelRepository extends JpaRepository<LanguageLevel, Long> {


    @Query("""
    SELECT l FROM LanguageLevel l
    WHERE l.id in :ids
""")
    public List<LanguageLevel> findByIds(List<Long> ids);

}
