package org.example.hirehub.controller;

import org.example.hirehub.dto.companydomain.CompanyDomainDTO;
import org.example.hirehub.dto.companydomain.CreateCompanyDomainDTO;
import org.example.hirehub.service.CompanyDomainService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/company-domains")
public class CompanyDomainController {

    private final CompanyDomainService companyDomainService;

    public CompanyDomainController(CompanyDomainService companyDomainService) {
        this.companyDomainService = companyDomainService;
    }

    @GetMapping("")
    public ResponseEntity<List<CompanyDomainDTO>> getAllDomains() {
        return ResponseEntity.ok(companyDomainService.getAllActiveDomains());
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyDomainDTO> createDomain(@RequestBody CreateCompanyDomainDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(companyDomainService.createDomain(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyDomainDTO> updateDomain(
            @PathVariable Long id, 
            @RequestBody CreateCompanyDomainDTO dto) {
        return ResponseEntity.ok(companyDomainService.updateDomain(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDomain(@PathVariable Long id) {
        companyDomainService.deleteDomain(id);
        return ResponseEntity.noContent().build();
    }
}

