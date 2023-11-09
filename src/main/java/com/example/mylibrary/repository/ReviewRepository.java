package com.example.mylibrary.repository;

import com.example.mylibrary.model.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>
{
    List<Review> findAllByBookId(Long bookId);

    Optional<Review> findByUserIdAndBookId(Long id, Long bookId);
}
