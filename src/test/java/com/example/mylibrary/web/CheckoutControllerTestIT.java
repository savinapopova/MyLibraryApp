package com.example.mylibrary.web;

import com.example.mylibrary.model.dto.CheckOutDTO;
import com.example.mylibrary.model.entity.Book;
import com.example.mylibrary.model.entity.Category;
import com.example.mylibrary.model.entity.Checkout;
import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.model.enums.CategoryName;
import com.example.mylibrary.repository.BookRepository;
import com.example.mylibrary.repository.CheckoutRepository;
import com.example.mylibrary.service.CategoryService;
import com.example.mylibrary.util.TimeConverter;
import com.example.mylibrary.utils.TestUserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CheckoutControllerTestIT {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    CheckoutRepository checkoutRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TestUserData testUserData;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    private CategoryService categoryService;

    private Book book1;

    private Book book2;

    private Book book3;

    private Book book4;

    private Book book5;

    private Book book6;

    private Checkout checkout1;

    private Checkout checkout2;

    private Checkout checkout3;

    private Checkout checkout4;

    private Checkout checkout5;


    private User user;

    private User admin;

    @BeforeEach
    void setUp() {
        this.checkoutRepository.deleteAll();
        this.bookRepository.deleteAll();
        this.testUserData.cleanUp();

        user = this.testUserData.createTestUser();
        admin = this.testUserData.createTestAdmin();

        Category biography = this.categoryService.getCategory(CategoryName.BIOGRAPHY);
        Category cookbook = this.categoryService.getCategory(CategoryName.COOKBOOK);

        book1 = new Book(1L, "title1", "author1",
                "image1", "description1", 1, 1, biography);
        book2 = new Book(2L, "title2", "author2",
                "image2", "description2", 2, 2, cookbook);
        book3 = new Book(3L, "title3", "author3",
                "image3", "description3", 0, 0, cookbook);
        book4 = new Book(4L, "title4", "author4",
                "image4", "description4", 2, 0, cookbook);
        book5 = new Book(5L, "title5", "author5",
                "image5", "description5", 2, 1, biography);
        book6 = new Book(6L, "title6", "author6",
                "image6", "description6", 6, 6, biography);

        checkout1 = new Checkout(book1, user);
        checkout2 = new Checkout(book2, user);
        checkout3 = new Checkout(book3, user);
        checkout4 = new Checkout(book4, user);
        checkout5 = new Checkout(book5, user);
    }

    @AfterEach
    void tearDown() {
        this.checkoutRepository.deleteAll();
        this.bookRepository.deleteAll();
        this.testUserData.cleanUp();
    }

    @Test
    void checkoutBookWhenAnonymous() throws Exception {
        this.bookRepository.save(book1);

        mockMvc.perform(MockMvcRequestBuilders.get("/checkout/book/{id}", book1.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void checkoutBook() throws Exception {

        this.bookRepository.save(book1);

        mockMvc.perform(MockMvcRequestBuilders.get("/checkout/book/{id}", book1.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout"))
                .andExpect(redirectedUrl(null))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attribute("booksCount", 0))
                .andExpect(model().attribute("alreadyCheckedOut", false))
                .andExpect(model().attribute("userBlocked", false));
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void checkoutBookWhenAlreadyCheckedOut() throws Exception {

        this.bookRepository.save(book1);
        this.checkoutRepository.save(checkout1);

        mockMvc.perform(MockMvcRequestBuilders.get("/checkout/book/{id}", book1.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout"))
                .andExpect(redirectedUrl(null))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attribute("booksCount", 1))
                .andExpect(model().attribute("alreadyCheckedOut", true))
                .andExpect(model().attribute("userBlocked", false));

    }

    @Test
    void testCheckoutBookWhenAnonymous() throws Exception {
        this.bookRepository.save(book1);

        mockMvc.perform(MockMvcRequestBuilders.post("/checkout/book/{id}", book1.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testCheckoutBook() throws Exception {

        this.bookRepository.save(book1);

        mockMvc.perform(MockMvcRequestBuilders.post("/checkout/book/{id}", book1.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checkout/book/" + book1.getId()));

        List<Checkout> checkouts = this.checkoutRepository.findAll();

        assertEquals(1, this.checkoutRepository.count());
        assertEquals(1, checkouts.get(0).getBook().getId());
        assertEquals("userEmail", checkouts.get(0).getUser().getEmail());
        assertEquals(book1.getTitle(), checkouts.get(0).getBook().getTitle());
        assertEquals(book1.getAuthor(), checkouts.get(0).getBook().getAuthor());
        assertEquals(book1.getImage(), checkouts.get(0).getBook().getImage());

    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testCheckoutBookWhenAlreadyCheckedOut() throws Exception {

        this.bookRepository.save(book1);
        this.checkoutRepository.save(checkout1);

        mockMvc.perform(MockMvcRequestBuilders.post("/checkout/book/{id}", book1.getId())
                        .with(csrf()))
                .andExpect(status().isMethodNotAllowed());

        List<Checkout> checkouts = this.checkoutRepository.findAll();

        assertEquals(1, this.checkoutRepository.count());
        assertEquals(1, checkouts.get(0).getBook().getId());
        assertEquals("userEmail", checkouts.get(0).getUser().getEmail());
        assertEquals(book1.getTitle(), checkouts.get(0).getBook().getTitle());
        assertEquals(book1.getAuthor(), checkouts.get(0).getBook().getAuthor());
        assertEquals(book1.getImage(), checkouts.get(0).getBook().getImage());

    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testCheckoutBookWhenTooMuchBooks() throws Exception {

        this.bookRepository.save(book1);
        this.bookRepository.save(book2);
        this.bookRepository.save(book3);
        this.bookRepository.save(book4);
        this.bookRepository.save(book5);
        this.bookRepository.save(book6);
        this.checkoutRepository.save(checkout1);
        this.checkoutRepository.save(checkout2);
        this.checkoutRepository.save(checkout3);
        this.checkoutRepository.save(checkout4);
        this.checkoutRepository.save(checkout5);

        mockMvc.perform(MockMvcRequestBuilders.post("/checkout/book/{id}", book6.getId())
                        .with(csrf()))
                .andExpect(status().isMethodNotAllowed());

        List<Checkout> checkouts = this.checkoutRepository.findAll();

        assertEquals(5, this.checkoutRepository.count());


    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testCheckoutBookWhenUserBlocked() throws Exception {

        this.bookRepository.save(book1);
        this.bookRepository.save(book2);
        checkout1.setReturnDate(LocalDate.now().minusDays(1));
        this.checkoutRepository.save(checkout1);

        mockMvc.perform(MockMvcRequestBuilders.post("/checkout/book/{id}", book2.getId())
                        .with(csrf()))
                .andExpect(status().isMethodNotAllowed());

        List<Checkout> checkouts = this.checkoutRepository.findAll();

        assertEquals(1, this.checkoutRepository.count());

    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testCheckoutBookWhenNoSuchBook() throws Exception {


        mockMvc.perform(MockMvcRequestBuilders.post("/checkout/book/{id}", 10L)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(view().name("object-not-found"))
                .andExpect(redirectedUrl(null))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message", "book with id 10 not found"));

        List<Checkout> checkouts = this.checkoutRepository.findAll();

        assertEquals(0, this.checkoutRepository.count());

    }

    @Test
    void testShelfWhenAnonymous() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/shelf"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
     void testShelf() throws Exception {

        this.bookRepository.save(book1);
        this.bookRepository.save(book2);
        this.bookRepository.save(book3);
        this.bookRepository.save(book4);
        this.bookRepository.save(book5);
        this.bookRepository.save(book6);
        this.checkoutRepository.save(checkout1);
        this.checkoutRepository.save(checkout2);
        this.checkoutRepository.save(checkout3);
        this.checkoutRepository.save(checkout4);
        this.checkoutRepository.save(checkout5);



        mockMvc.perform(MockMvcRequestBuilders.get("/shelf"))
                .andExpect(status().isOk())
                .andExpect(view().name("shelf"))
                .andExpect(redirectedUrl(null))
                .andExpect(model().attributeExists("loans"))
                .andExpect(model().attribute("loans", hasSize(5)))
                .andExpect(model().attributeExists("histories"))
                .andExpect(model().attribute("histories", hasSize(0)));
    }

}