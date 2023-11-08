package com.example.mylibrary.service.impl;

import com.example.mylibrary.model.dto.ReviewDTO;
import com.example.mylibrary.model.entity.Review;
import com.example.mylibrary.repository.ReviewRepository;
import com.example.mylibrary.service.ReviewService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private ReviewRepository reviewRepository;
    private ModelMapper modelMapper;

    public ReviewServiceImpl(ReviewRepository reviewRepository, ModelMapper modelMapper) {
        this.reviewRepository = reviewRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ReviewDTO> getByBook(Long bookId) {

       List<Review> reviews = this.reviewRepository.findAllByBookId(bookId);
     return reviews.stream().map(r -> modelMapper.map(r, ReviewDTO.class))
               .collect(Collectors.toList());


    }
}
