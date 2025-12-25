package org.example.hirehub.repository;

import org.example.hirehub.entity.User;
import org.example.hirehub.entity.UserEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserEmbeddingRepository extends JpaRepository<UserEmbedding, Long> {

    Optional<UserEmbedding> findByUser(User user);

    Optional<UserEmbedding> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM UserEmbedding ue WHERE ue.user.id = :userId")
    void deleteByUserId(Long userId);

    List<UserEmbedding> findAllByEmbeddingIsNotNull();
}
