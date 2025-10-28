package org.example.hirehub.repository;

import org.example.hirehub.dto.user.UserDetailDTO;
import org.example.hirehub.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.example.hirehub.entity.User;

import java.time.LocalDateTime;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value= """
    SELECT u from User u
    JOIN FETCH  u.role ur
    JOIN FETCH ur.rolePermission urp
    JOIN FETCH urp.permission p
    WHERE u.email = ?1
""")
    User findByEmail(String email);

    @Query("SELECT j FROM User j " +
            "JOIN j.role r " +
            "WHERE (:role IS NULL OR r.name = :role) " +
            "AND (:province IS NULL OR j.address LIKE %:province%) " +
            "AND (:keyword IS NULL OR (" +
            "j.name LIKE %:keyword% " +
            ")) " )
    Page<User> findAll(@Param("keyword") String keyword,
                       @Param("province") String province,
                       @Param("role") String role,
                       Pageable pageable);

}

