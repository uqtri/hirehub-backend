package org.example.hirehub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.example.hirehub.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

}
