package org.example.hirehub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

import org.example.hirehub.entity.Role;

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

    @Query(value = """
    SELECT r FROM Role r
    WHERE r.name=?1
""")
    Optional<Role> findByName(String name);
}
