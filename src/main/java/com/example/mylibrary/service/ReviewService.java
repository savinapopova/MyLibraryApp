package com.example.mylibrary.service;

import com.example.mylibrary.model.dto.review.LeaveReviewDTO;
import com.example.mylibrary.model.dto.review.ReviewDTO;

import java.util.List;

public interface ReviewService {
    List<ReviewDTO> getByBook(Long bookId);

    boolean reviewLeft(String userEmail, Long bookId);

    void registerReview(LeaveReviewDTO leaveReviewDTO, String userEmail, Long bookId);

    void deleteBookReviews(Long id);

    void deleteUserReviews(Long userId);
}
