package com.example.mylibrary.service;

import com.example.mylibrary.model.dto.LeaveReviewDTO;
import com.example.mylibrary.model.dto.ReviewDTO;

import java.security.Principal;
import java.util.List;

public interface ReviewService {
    List<ReviewDTO> getByBook(Long bookId);

    boolean reviewLeft(String userEmail, Long bookId);

    void registerReview(LeaveReviewDTO leaveReviewDTO, String userEmail, Long bookId);

    void deleteBookReviews(Long id);

    void deleteUserReviews(Long userId);
}
