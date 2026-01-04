package org.example.hirehub.service;

import org.example.hirehub.dto.companydomain.CompanyDomainDTO;
import org.example.hirehub.dto.companydomain.CreateCompanyDomainDTO;
import org.example.hirehub.entity.CompanyDomain;
import org.example.hirehub.repository.CompanyDomainRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyDomainService {

    private final CompanyDomainRepository companyDomainRepository;

    public CompanyDomainService(CompanyDomainRepository companyDomainRepository) {
        this.companyDomainRepository = companyDomainRepository;
    }

    public List<CompanyDomainDTO> getAllActiveDomains() {
        return companyDomainRepository.findAllActive().stream()
            .map(domain -> new CompanyDomainDTO(domain.getId(), domain.getDomain()))
            .collect(Collectors.toList());
    }

    @Transactional
    public CompanyDomainDTO createDomain(CreateCompanyDomainDTO dto) {
        if (companyDomainRepository.existsByDomain(dto.getDomain())) {
            throw new RuntimeException("Domain already exists");
        }
        
        CompanyDomain domain = new CompanyDomain();
        domain.setDomain(dto.getDomain());
        CompanyDomain saved = companyDomainRepository.save(domain);
        
        return new CompanyDomainDTO(saved.getId(), saved.getDomain());
    }

    @Transactional
    public CompanyDomainDTO updateDomain(Long id, CreateCompanyDomainDTO dto) {
        CompanyDomain domain = companyDomainRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Domain not found"));
        
        if (!domain.getDomain().equals(dto.getDomain()) && 
            companyDomainRepository.existsByDomain(dto.getDomain())) {
            throw new RuntimeException("Domain already exists");
        }
        
        domain.setDomain(dto.getDomain());
        CompanyDomain updated = companyDomainRepository.save(domain);
        
        return new CompanyDomainDTO(updated.getId(), updated.getDomain());
    }

    @Transactional
    public void deleteDomain(Long id) {
        CompanyDomain domain = companyDomainRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Domain not found"));
        
        domain.setDeleted(true);
        companyDomainRepository.save(domain);
    }
}






