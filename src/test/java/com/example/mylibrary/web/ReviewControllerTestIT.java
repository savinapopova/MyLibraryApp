package com.example.mylibrary.web;

import com.example.mylibrary.model.entity.*;
import com.example.mylibrary.model.enums.CategoryName;
import com.example.mylibrary.repository.BookRepository;
import com.example.mylibrary.repository.CategoryRepository;
import com.example.mylibrary.repository.CheckoutRepository;
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

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReviewControllerTestIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUserData testUserData;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CheckoutRepository checkoutRepository;

    private User user;

    private Book book1;

    private Book book2;

    private Checkout checkout1;

    private Checkout checkout2;

    @BeforeEach
    public void setUp() {
        this.reviewRepository.deleteAll();
        this.checkoutRepository.deleteAll();
        this.bookRepository.deleteAll();
        this.testUserData.cleanUp();

        user = testUserData.createTestUser();

        Category biography = this.categoryService.getCategory(CategoryName.BIOGRAPHY);
        Category cookbook = this.categoryService.getCategory(CategoryName.COOKBOOK);

        book1 = new Book(1L, "title1", "author1",
                "image1", "description1", 1, 1, biography);
        book2 = new Book(2L, "title2", "author2",
                "image2", "description2", 2, 2, cookbook);

        checkout1 = new Checkout(book1, user);
        checkout2 = new Checkout(book2, user);
    }

    @AfterEach
    public void cleanUp() {
        this.reviewRepository.deleteAll();
        this.checkoutRepository.deleteAll();
        this.bookRepository.deleteAll();
        this.testUserData.cleanUp();

    }

    @Test
    void testLeaveReviewGetWhenAnonymous() throws Exception {
        this.bookRepository.save(book1);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/reviews/form/{id}", book1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService",
            setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testLeaveReviewGet() throws Exception {
        this.bookRepository.save(book1);
        this.checkoutRepository.save(checkout1);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/reviews/form/{id}", book1.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("review-add"))
                .andExpect(redirectedUrl(null))
                .andExpect(model().attributeExists("bookId"))
                .andExpect(model().attribute("bookId", book1.getId()))
                .andExpect(model().attributeExists("reviewLeft"))
                .andExpect(model().attribute("reviewLeft", false));

    }

    @Test
    void testLeaveReviewPostWhenAnonymous() throws Exception {
        this.bookRepository.save(book1);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/reviews/leave/{id}", book1.getId())
                .param("rating", "5")
                .param("comment", "comment")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService",
            setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testLeaveReviewPost() throws Exception {
        this.bookRepository.save(book1);
        this.checkoutRepository.save(checkout1);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/reviews/leave/{id}", book1.getId())
                .param("rating", "5")
                .param("comment", "comment")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reviews/1"));


        assertEquals(1, this.reviewRepository.count());
        assertEquals(5, this.reviewRepository.findAll().get(0).getRating());
        assertEquals("comment", this.reviewRepository.findAll().get(0).getComment());
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService",
            setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testLeaveReviewPostWithNoRating() throws Exception {
        this.bookRepository.save(book1);
        this.checkoutRepository.save(checkout1);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/reviews/leave/{id}", book1.getId())
                .param("rating", "")
                .param("comment", "comment")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reviews/form/1"))
                .andExpect(flash().attributeExists("leaveReviewDTO"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.leaveReviewDTO"))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.leaveReviewDTO",
                        hasProperty("fieldErrors",
                                hasItem(hasProperty("field", is("rating"))))))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.leaveReviewDTO",
                        hasProperty("fieldErrors",
                                hasItem(hasProperty("defaultMessage", is("Rating is required!"))))));

        assertEquals(0, this.reviewRepository.count());
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService",
            setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testLeaveReviewWhenNoCheckout() throws Exception {

        this.bookRepository.save(book1);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/reviews/leave/{id}", book1.getId())
                .param("rating", "5")
                .param("comment", "comment")
                .with(csrf()))
                .andExpect(status().isMethodNotAllowed());

        assertEquals(0, this.reviewRepository.count());

    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService",
            setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testLeaveReviewWhenAlreadyLeft() throws Exception {

        this.bookRepository.save(book1);
        this.checkoutRepository.save(checkout1);
        this.reviewRepository.save(new Review(5,user,  book1));

        this.mockMvc.perform(MockMvcRequestBuilders.post("/reviews/leave/{id}", book1.getId())
                        .param("rating", "4")
                        .param("comment", "comment")
                        .with(csrf()))
                .andExpect(status().isMethodNotAllowed());

        assertEquals(1, this.reviewRepository.count());

    }

    @Test
    void testShowReviewsWhenAnonymous() throws Exception {
        this.bookRepository.save(book1);
        this.reviewRepository.save(new Review(5,user,  book1));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/reviews/{id}", book1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService",
            setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testShowReviews() throws Exception {
        this.bookRepository.save(book1);
        this.reviewRepository.save(new Review(5,user,  book1));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/reviews/{id}", book1.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("reviews"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attribute("book", hasProperty("id", is(book1.getId()))))
                .andExpect(model().attribute("book", hasProperty("title", is(book1.getTitle()))))
                .andExpect(model().attribute("book", hasProperty("author", is(book1.getAuthor()))))
                .andExpect(model().attribute("book", hasProperty("image", is(book1.getImage()))))
                .andExpect(model().attribute("book", hasProperty("description", is(book1.getDescription()))))
                .andExpect(model().attributeExists("reviews"))
                .andExpect(model().attribute("reviews", hasSize(1)))
                .andExpect(model().attribute("reviews", hasItem(
                        allOf(
                                hasProperty("rating", is(5.0))
                        )
                )));
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService",
            setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testShowReviewsWhenNoSuchBook() throws Exception {



        this.mockMvc.perform(MockMvcRequestBuilders.post("/reviews/leave/{id}", 10L)
                        .param("rating", "4")
                        .param("comment", "comment")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(view().name("object-not-found"))
                .andExpect(redirectedUrl(null))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message", "book with id 10 not found"))
        ;

        assertEquals(0, this.reviewRepository.count());
    }





}