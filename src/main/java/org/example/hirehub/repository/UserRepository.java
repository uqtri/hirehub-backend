package org.example.hirehub.repository;

import org.example.hirehub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

//    @Override
//    @Query(value = """
//    SELECT u.id, u.email, u.avatar, u.address, u.description,
//                           u.is_verified, u.is_banned
//    FROM "user" u
//    LEFT JOIN  ROLE r ON u.id = r.id
//    LEFT JOIN ROLE_PERMISSION rp ON rp.role_id = r.id
//    LEFT JOIN PERMISSION p ON p.id = rp.permission_id
//""", nativeQuery = true)
//    List<User> findAll();
    @NonNull
    @Query("""
        SELECT DISTINCT u FROM User u
        LEFT JOIN FETCH u.role r
        LEFT JOIN FETCH r.rolePermission rp
        LEFT JOIN FETCH rp.permission
    """)
    List<User> findAll();
}

