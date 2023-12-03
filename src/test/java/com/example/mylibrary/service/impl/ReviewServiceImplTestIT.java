package com.example.mylibrary.service.impl;

import com.example.mylibrary.exceptions.NotAllowedException;
import com.example.mylibrary.exceptions.ObjectNotFoundException;
import com.example.mylibrary.model.dto.review.LeaveReviewDTO;
import com.example.mylibrary.model.dto.review.ReviewDTO;
import com.example.mylibrary.model.entity.*;
import com.example.mylibrary.model.enums.CategoryName;
import com.example.mylibrary.repository.BookRepository;
import com.example.mylibrary.repository.CheckoutRepository;
import com.example.mylibrary.repository.ReviewRepository;
import com.example.mylibrary.service.CategoryService;
import com.example.mylibrary.service.ReviewService;
import com.example.mylibrary.utils.TestUserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReviewServiceImplTestIT {

    @Autowired
    private ReviewService serviceToTest;

    @Autowired
    private ReviewRepository reviewRepository;


    @Autowired
    private CheckoutRepository checkoutRepository;


    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestUserData testUserData;

    @Autowired
    private CategoryService categoryService;

    private User user;

    private User admin;

    private Book book1;

    private Book book2;

    private Checkout checkout1;

    private Checkout checkout2;

    private Checkout checkout3;


    @BeforeEach
    public void setUp() {
        reviewRepository.deleteAll();
        checkoutRepository.deleteAll();
        bookRepository.deleteAll();
       testUserData.cleanUp();

        user = testUserData.createTestUser();
        admin = testUserData.createTestAdmin();



       Category biography = this.categoryService.getCategory(CategoryName.BIOGRAPHY);
        Category cookbook = this.categoryService.getCategory(CategoryName.COOKBOOK);

        book1 = new Book(1L, "title1", "author1",
                "image1", "description1", 1, 1, biography);
        book2 = new Book(2L, "title2", "author2",
                "image2", "description2", 2, 2, cookbook);

        checkout1 = new Checkout(book1, user);
        checkout2 = new Checkout(book2, user);
        checkout3 = new Checkout(book2, admin);


    }

    @AfterEach
    public void tearDown() {
        reviewRepository.deleteAll();
        checkoutRepository.deleteAll();
        bookRepository.deleteAll();
        testUserData.cleanUp();
    }

    @Test
    @Transactional
    void testRegisterReview() {
        this.bookRepository.save(book1);
        this.checkoutRepository.save(checkout1);

        LeaveReviewDTO leaveReviewDTO = new LeaveReviewDTO(5, "comment");
        this.serviceToTest.registerReview(leaveReviewDTO, user.getEmail(), book1.getId());
        assertEquals(1, this.reviewRepository.count());
        Review review = this.reviewRepository.findAll().get(0);
        assertEquals(5, review.getRating());
        assertEquals("comment", review.getComment());

    }

    @Test
    @Transactional
    void testRegisterReviewWhenAlreadyLeft() {
        this.bookRepository.save(book1);
        this.checkoutRepository.save(checkout1);

        LeaveReviewDTO leaveReviewDTO = new LeaveReviewDTO(5, "comment");
        this.serviceToTest.registerReview(leaveReviewDTO, user.getEmail(), book1.getId());


        LeaveReviewDTO leaveReviewDTO2 = new LeaveReviewDTO(5, "comment2");

        assertThrows(NotAllowedException.class, () -> {
            this.serviceToTest.registerReview(leaveReviewDTO2, user.getEmail(), book1.getId());
        });

    }

    @Test
    @Transactional
    void registerReviewWhenNoSuchCheckout() {
        this.bookRepository.save(book1);
        this.bookRepository.save(book2);
        this.checkoutRepository.save(checkout1);

        LeaveReviewDTO leaveReviewDTO = new LeaveReviewDTO(5, "comment");
        this.serviceToTest.registerReview(leaveReviewDTO, user.getEmail(), book1.getId());

        LeaveReviewDTO leaveReviewDTO2 = new LeaveReviewDTO(5, "comment2");

        assertThrows(NotAllowedException.class, () -> {
            this.serviceToTest.registerReview(leaveReviewDTO2, user.getEmail(), book2.getId());
        });

    }

    @Test
    @Transactional
    void testRegisterReviewWhenBookNotExisting() {
        LeaveReviewDTO leaveReviewDTO = new LeaveReviewDTO(5, "comment");
        assertThrows(ObjectNotFoundException.class, () -> {
            this.serviceToTest.registerReview(leaveReviewDTO, user.getEmail(), 100L);
        });

        try {
            this.serviceToTest.registerReview(leaveReviewDTO, user.getEmail(), 100L);
        } catch (ObjectNotFoundException exception) {
            assertEquals("book with id 100 not found", exception.getMessage());
        }
    }

    @Test
    @Transactional
    void testGetByBook() {
        this.bookRepository.save(book1);
        this.bookRepository.save(book2);
        this.checkoutRepository.save(checkout1);
        this.checkoutRepository.save(checkout2);

        this.serviceToTest.registerReview(new LeaveReviewDTO(5, "comment1"),
                user.getEmail(), book1.getId());
        this.serviceToTest.registerReview(new LeaveReviewDTO(4, "comment2"),
                user.getEmail(), book2.getId());

        List<ReviewDTO> reviews = this.serviceToTest.getByBook(book1.getId());
        assertEquals(1, reviews.size());

    }

    @Test
    @Transactional
    void testReviewLeft() {
        this.bookRepository.save(book1);
        this.bookRepository.save(book2);
        this.checkoutRepository.save(checkout1);
        this.checkoutRepository.save(checkout2);
        this.checkoutRepository.save(checkout3);

        this.serviceToTest.registerReview(new LeaveReviewDTO(5, "comment1"),
                user.getEmail(), book1.getId());
        this.serviceToTest.registerReview(new LeaveReviewDTO(4, "comment2"),
                admin.getEmail(), book2.getId());

        assertTrue(this.serviceToTest.reviewLeft(user.getEmail(), book1.getId()));
        assertFalse(this.serviceToTest.reviewLeft(admin.getEmail(), book1.getId()));
        assertTrue(this.serviceToTest.reviewLeft(admin.getEmail(), book2.getId()));
        assertFalse(this.serviceToTest.reviewLeft(user.getEmail(), book2.getId()));
    }

    @Test
    @Transactional
    void testDeleteBookReviews() {
        this.bookRepository.save(book1);
        this.bookRepository.save(book2);
        this.checkoutRepository.save(checkout1);
        this.checkoutRepository.save(checkout2);
        this.checkoutRepository.save(checkout3);

        this.serviceToTest.registerReview(new LeaveReviewDTO(5, "comment1"),
                user.getEmail(), book1.getId());
        this.serviceToTest.registerReview(new LeaveReviewDTO(4, "comment2"),
                admin.getEmail(), book2.getId());

        assertEquals(2, this.reviewRepository.count());
        this.serviceToTest.deleteBookReviews(book1.getId());
        assertEquals(1, this.reviewRepository.count());
        assertEquals(admin.getEmail(), this.reviewRepository.findAll().get(0).getUser().getEmail());
    }

    @Test
    @Transactional
    void testDeleteUserReviews() {
        this.bookRepository.save(book1);
        this.bookRepository.save(book2);
        this.checkoutRepository.save(checkout1);
        this.checkoutRepository.save(checkout2);
        this.checkoutRepository.save(checkout3);

        this.serviceToTest.registerReview(new LeaveReviewDTO(5, "comment1"),
                user.getEmail(), book1.getId());
        this.serviceToTest.registerReview(new LeaveReviewDTO(4, "comment2"),
                admin.getEmail(), book2.getId());

        assertEquals(2, this.reviewRepository.count());
        this.serviceToTest.deleteUserReviews(user.getId());
        assertEquals(1, this.reviewRepository.count());
        assertEquals(admin.getEmail(), this.reviewRepository.findAll().get(0).getUser().getEmail());
    }

}