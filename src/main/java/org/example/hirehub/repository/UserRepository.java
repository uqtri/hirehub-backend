package org.example.hirehub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.example.hirehub.entity.User;

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

}

