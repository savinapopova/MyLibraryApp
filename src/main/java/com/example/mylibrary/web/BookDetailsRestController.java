package com.example.mylibrary.web;

import com.example.mylibrary.model.dto.ReviewDTO;
import com.example.mylibrary.model.dto.SearchBookDTO;
import com.example.mylibrary.service.BookService;
import com.example.mylibrary.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BookDetailsRestController {

    private BookService bookService;
    private ReviewService reviewService;

    public BookDetailsRestController(BookService bookService, ReviewService reviewService) {
        this.bookService = bookService;
        this.reviewService = reviewService;
    }

    @GetMapping("/api/books/details/{id}")
    public ResponseEntity<SearchBookDTO> getBookById(@PathVariable Long id) {
        SearchBookDTO book = this.bookService.getSearchBookDTO(id);
        if (book != null) {
            return ResponseEntity.ok(book);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("api/reviews/book/{bookId}")
    public ResponseEntity<List<ReviewDTO>> getBookReviews(@PathVariable Long bookId) {
        List<ReviewDTO> reviews = this.reviewService.getByBook(bookId);
        SearchBookDTO book = this.bookService.getSearchBookDTO(bookId);
        if (reviews != null && book != null) {
            return ResponseEntity.ok(reviews);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
