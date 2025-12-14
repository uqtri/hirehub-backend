package org.example.hirehub.repository;

import org.example.hirehub.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FcmTokenRepository extends JpaRepository<FcmToken, String> {
    List<FcmToken> findByUserId(Long userId);
}
