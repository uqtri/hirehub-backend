package org.example.hirehub.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.example.hirehub.entity.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

        @Query(value = """
                            SELECT u from User u
                            LEFT JOIN FETCH  u.role ur
                            LEFT JOIN FETCH ur.rolePermission urp
                            LEFT JOIN FETCH urp.permission p
                            WHERE u.email = ?1
                        """)
        User findByEmail(String email);

        @Query("SELECT j FROM User j " +
                        "JOIN j.role r " +
                        "WHERE (:role IS NULL OR r.name = :role) " +
                        "AND (:province IS NULL OR j.address LIKE %:province%) " +
                        "AND (:keyword IS NULL OR (" +
                        "j.name LIKE %:keyword% " +
                        ")) " +
                        "AND (:status IS NULL " +
                        "OR (:status = 'verified' AND j.isVerified = true AND j.isBanned = false) " +
                        "OR (:status = 'pending' AND j.isVerified = false AND j.isBanned = false) " +
                        "OR (:status = 'banned' AND j.isBanned = true)) ")
        Page<User> findAll(@Param("keyword") String keyword,
                        @Param("province") String province,
                        @Param("role") String role,
                        @Param("status") String status,
                        Pageable pageable);

        @Query("""
                        SELECT DISTINCT u FROM User u
                        LEFT JOIN FETCH u.userSkills us
                        LEFT JOIN FETCH us.skill
                        WHERE u.isDeleted = false
                        """)
        List<User> findAllByIsDeletedFalseWithDetails();

        @Query("""
                        SELECT u FROM User u
                        LEFT JOIN FETCH u.userSkills us
                        LEFT JOIN FETCH us.skill
                        WHERE u.id = :id
                        """)
        java.util.Optional<User> findByIdWithDetails(@Param("id") Long id);
}
