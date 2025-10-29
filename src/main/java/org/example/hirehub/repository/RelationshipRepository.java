package org.example.hirehub.repository;

import org.example.hirehub.entity.Relationship;
import org.example.hirehub.key.RelationshipKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RelationshipRepository extends JpaRepository<Relationship, RelationshipKey>, JpaSpecificationExecutor<Relationship> {

    @Query("""
        SELECT r FROM Relationship r
        WHERE r.userA.id = ?1 or r.userB.id = ?1
""")
    List<Relationship> findRelationshipsByUserId(Long userId);
}
