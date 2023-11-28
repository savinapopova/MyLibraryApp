package com.example.mylibrary.web;

import com.example.mylibrary.model.entity.Book;
import com.example.mylibrary.model.entity.Category;
import com.example.mylibrary.model.entity.Review;
import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.model.enums.CategoryName;
import com.example.mylibrary.repository.BookRepository;
import com.example.mylibrary.repository.ReviewRepository;
import com.example.mylibrary.service.CategoryService;
import com.example.mylibrary.utils.TestUserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookDetailsRestControllerTestIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUserData testUserData;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CategoryService categoryService;

    private Book book1;

    private Book book2;

    private User user;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        bookRepository.deleteAll();
        testUserData.cleanUp();

        user = testUserData.createTestUser();

        Category biography = this.categoryService.getCategory(CategoryName.BIOGRAPHY);
        Category cookbook = this.categoryService.getCategory(CategoryName.COOKBOOK);

        book1 = new Book(1L, "title1", "author1",
                "image1", "description1", 1, 1, biography);
        book2 = new Book(2L, "title2", "author2",
                "image2", "description2", 2, 2, cookbook);
    }

    @AfterEach
    void tearDown() {
        reviewRepository.deleteAll();
        bookRepository.deleteAll();
        testUserData.cleanUp();


    }

    @Test
    void testGetBookDetailsWhenAnonymous() throws Exception {
        this.bookRepository.save(book1);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/books/details/{id}", book1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testGetBookDetails() throws Exception {

        this.bookRepository.save(book1);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/books/details/{id}", book1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(book1.getId()))
                .andExpect(jsonPath("$.title").value(book1.getTitle()))
                .andExpect(jsonPath("$.author").value(book1.getAuthor()))
                .andExpect(jsonPath("$.image").value(book1.getImage()))
                .andExpect(jsonPath("$.description").value(book1.getDescription()));

    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testGetBookDetailsWithInvalidId() throws Exception {

        this.bookRepository.save(book1);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/books/details/{id}", 2L))
                .andExpect(status().isNotFound());

    }

    @Test
    void testGetBookReviewsWhenAnonymous() throws Exception {
        this.bookRepository.save(book1);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/books/reviews/{id}", book1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testGetBookReviews() throws Exception {

        this.bookRepository.save(book1);
        Review review = new Review(5, user, book1);
        review.setComment("comment");
        this.reviewRepository.save(review);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/reviews/book/{bookId}", book1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].rating").value(5))
                .andExpect(jsonPath("$[0].comment").value("comment"))
                .andExpect(jsonPath("$[0].userEmail").value(user.getEmail()));

    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testGetBookReviewsWithInvalidId() throws Exception {


        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/reviews/book/{bookId}", 2L))
                .andExpect(status().isNotFound());

    }
}