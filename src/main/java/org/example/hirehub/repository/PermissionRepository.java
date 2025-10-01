package org.example.hirehub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.example.hirehub.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    @Query("""
    SELECT p from Permission p
    WHERE p.isDeleted = false
""")
    public List<Permission> findAll();
}
