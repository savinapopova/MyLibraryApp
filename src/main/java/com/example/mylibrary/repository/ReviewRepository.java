package com.example.mylibrary.repository;

import com.example.mylibrary.model.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>
{
    List<Review> findAllByBookIdOrderByDateDesc(Long bookId);

    Optional<Review> findByUserEmailAndBookId(String userEmail, Long bookId);

    void deleteAllByBookId(Long id);

    void deleteAllByUserId(Long id);
}
