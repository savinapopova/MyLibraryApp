package com.example.mylibrary.repository;

import com.example.mylibrary.model.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByUserEmail(String userEmail);

    List<Message> findAllByClosed(boolean closed);

    void deleteAllByUserId(Long id);
}
