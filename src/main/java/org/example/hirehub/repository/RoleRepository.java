package org.example.hirehub.repository;

import org.example.hirehub.dto.role.RoleDetailDTO;
import org.example.hirehub.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Override
    @NonNull
    @Query("""
    SELECT r FROM Role r
    LEFT JOIN FETCH r.rolePermission rp
    LEFT JOIN FETCH rp.role
""")
    List<Role> findAll();
}
