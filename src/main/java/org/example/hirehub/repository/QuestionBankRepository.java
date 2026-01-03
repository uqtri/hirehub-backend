package org.example.hirehub.repository;

import org.example.hirehub.entity.QuestionBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionBankRepository extends JpaRepository<QuestionBank, Long> {
    List<QuestionBank> findByRecruiterIdAndIsDeletedFalse(Long recruiterId);
    
    List<QuestionBank> findByRecruiterIdAndCategoryAndIsDeletedFalse(Long recruiterId, String category);
}

