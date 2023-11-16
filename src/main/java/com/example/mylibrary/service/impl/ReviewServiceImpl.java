package com.example.mylibrary.service.impl;

import com.example.mylibrary.model.dto.LeaveReviewDTO;
import com.example.mylibrary.model.dto.ReviewDTO;
import com.example.mylibrary.model.entity.Book;
import com.example.mylibrary.model.entity.Review;
import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.repository.ReviewRepository;
import com.example.mylibrary.service.BookService;
import com.example.mylibrary.service.ReviewService;
import com.example.mylibrary.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private ReviewRepository reviewRepository;

    private UserService userService;

    private BookService bookService;
    private ModelMapper modelMapper;

    public ReviewServiceImpl(ReviewRepository reviewRepository, UserService userService,
                             BookService bookService, ModelMapper modelMapper) {
        this.reviewRepository = reviewRepository;
        this.userService = userService;
        this.bookService = bookService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ReviewDTO> getByBook(Long bookId) {

       List<Review> reviews = this.reviewRepository.findAllByBookIdOrderByDateDesc(bookId);
     return reviews.stream().map(r -> modelMapper.map(r, ReviewDTO.class))
               .collect(Collectors.toList());


    }

    @Override
    public boolean reviewLeft(Principal principal, Long bookId) {
        User user = this.userService.getLoggedUser(principal);
        Optional<Review> optionalReview = this.reviewRepository.findByUserIdAndBookId(user.getId(), bookId);
        if (optionalReview.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public void registerReview(LeaveReviewDTO leaveReviewDTO, Principal principal, Long bookId) {
        Review review = modelMapper.map(leaveReviewDTO, Review.class);
        User user = this.userService.getLoggedUser(principal);
        Book book = this.bookService.getBook(bookId);
        review.setBook(book);
        review.setUser(user);
        review.setDate(LocalDate.now());
        this.reviewRepository.save(review);
    }

    @Override
    public void deleteBookReviews(Long id) {
        this.reviewRepository.deleteAllByBookId(id);
    }

    @Override
    public void deleteUserReviews(Long userId) {
        this.reviewRepository.deleteAllByUserId(userId);
    }
}
