package com.example.mylibrary.service;

import com.example.mylibrary.model.dto.ReviewDTO;

import java.util.List;

public interface ReviewService {
    List<ReviewDTO> getByBook(Long bookId);
}
