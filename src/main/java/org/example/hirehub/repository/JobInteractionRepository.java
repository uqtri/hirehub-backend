package org.example.hirehub.repository;

import org.example.hirehub.entity.JobInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface JobInteractionRepository extends JpaRepository<JobInteraction, Long>, JpaSpecificationExecutor<JobInteraction> {

}
