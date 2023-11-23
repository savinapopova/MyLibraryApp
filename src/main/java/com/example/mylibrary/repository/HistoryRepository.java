package com.example.mylibrary.repository;

import com.example.mylibrary.model.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findAllByUserEmailOrderByReturnDateDesc(String userEmail);

    void deleteAllByBookId(Long id);

    void deleteAllByUserId(Long id);
}
