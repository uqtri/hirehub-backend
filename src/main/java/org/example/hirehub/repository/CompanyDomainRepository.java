package org.example.hirehub.repository;

import org.example.hirehub.entity.CompanyDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyDomainRepository extends JpaRepository<CompanyDomain, Long> {
    
    @Query("SELECT cd FROM CompanyDomain cd WHERE cd.isDeleted = false")
    List<CompanyDomain> findAllActive();
    
    Optional<CompanyDomain> findByDomain(String domain);
    
    boolean existsByDomain(String domain);
}



