package org.example.hirehub.repository;

import org.example.hirehub.entity.Job;
import org.example.hirehub.entity.JobEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobEmbeddingRepository extends JpaRepository<JobEmbedding, Long> {

    Optional<JobEmbedding> findByJob(Job job);

    Optional<JobEmbedding> findByJobId(Long jobId);

    boolean existsByJobId(Long jobId);

    @Modifying
    @Query("DELETE FROM JobEmbedding je WHERE je.job.id = :jobId")
    void deleteByJobId(Long jobId);

    List<JobEmbedding> findAllByEmbeddingIsNotNull();
}
