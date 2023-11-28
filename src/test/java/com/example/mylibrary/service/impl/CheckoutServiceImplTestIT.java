package com.example.mylibrary.service.impl;

import com.example.mylibrary.exceptions.NotAllowedException;
import com.example.mylibrary.exceptions.ObjectNotFoundException;
import com.example.mylibrary.model.dto.CheckOutDTO;
import com.example.mylibrary.model.entity.*;
import com.example.mylibrary.model.enums.CategoryName;
import com.example.mylibrary.repository.BookRepository;
import com.example.mylibrary.repository.CheckoutRepository;
import com.example.mylibrary.repository.HistoryRepository;
import com.example.mylibrary.service.CategoryService;
import com.example.mylibrary.service.CheckoutService;
import com.example.mylibrary.utils.TestUserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CheckoutServiceImplTestIT {

    @Autowired
    private CheckoutService serviceToTest;

    @Autowired
    private CheckoutRepository checkoutRepository;


    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestUserData testUserData;


    @Autowired
    private HistoryRepository historyRepository;


    private Book book1;

    private Book book2;

    private Book book3;

    private Book book4;

    private Book book5;

    private Book book6;

    private User user;

    private User admin;


    private Checkout checkout1;

    private Checkout checkout2;

    private Checkout checkout3;

    private Checkout checkout4;

    private Checkout checkout5;

    @BeforeEach
    void setUp() {
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
        checkoutRepository.deleteAll();
        bookRepository.deleteAll();
        testUserData.cleanUp();
    }

    @Test
    void testCheckoutBook() {
        this.bookRepository.save(book1);

        long count = this.checkoutRepository.count();
        assertEquals(0, count);

        serviceToTest.checkoutBook(book1.getId(), user.getEmail());
        assertEquals(1, checkoutRepository.count());
        assertEquals(0, bookRepository.findById(book1.getId()).get().getCopiesAvailable());
    }

    @Test
    void testCheckoutAlreadyCheckedOutBook() {
        this.bookRepository.save(book1);
        this.checkoutRepository.save(checkout1);

        assertThrows(NotAllowedException.class, () -> {
            serviceToTest.checkoutBook(book1.getId(), user.getEmail());
        });
    }

    @Test
    void testCheckoutWhenAlreadyFiveBook() {
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

        assertThrows(NotAllowedException.class, () -> {
            serviceToTest.checkoutBook(book6.getId(), user.getEmail());
        });
    }

    @Test
    void testCheckoutBookWhenNoCopiesAvailable() {
        this.bookRepository.save(book4);


        assertThrows(NotAllowedException.class, () -> {
            serviceToTest.checkoutBook(book4.getId(), user.getEmail());
        });
    }

    @Test
    void testGetUserCheckouts() {
        this.bookRepository.save(book1);
        this.bookRepository.save(book2);

        this.checkoutRepository.save(checkout1);
        this.checkoutRepository.save(checkout2);

        List<CheckOutDTO> checkouts = this.serviceToTest.getUserCheckouts(user.getId());
        assertEquals(2, checkouts.size());
        assertEquals(book1.getTitle(), checkouts.get(0).getBook().getTitle());
        assertEquals(book2.getTitle(), checkouts.get(1).getBook().getTitle());

    }

    @Test
    @Transactional
    void testReturnBook() {
        this.bookRepository.save(book1);
        this.checkoutRepository.save(checkout1);

        assertEquals(1, this.checkoutRepository.count());
        assertEquals(0, this.historyRepository.count());
        assertEquals(1, this.bookRepository.findById(book1.getId()).get().getCopiesAvailable());

        this.serviceToTest.returnBook(book1.getId(), user.getEmail());
        assertEquals(0, this.checkoutRepository.count());
        assertEquals(1, this.historyRepository.count());
        assertEquals(2, this.bookRepository.findById(book1.getId()).get().getCopiesAvailable());
    }

    @Test
    void testReturnBookWhenBookNotFound() {
        this.bookRepository.save(book1);
        this.checkoutRepository.save(checkout1);

        assertThrows(ObjectNotFoundException.class, () -> {
            this.serviceToTest.returnBook(book2.getId(), user.getEmail());
        });
    }

    @Test
void testReturnBookWhenCheckoutNotFound() {
        this.bookRepository.save(book1);
        this.checkoutRepository.save(checkout1);

        assertThrows(ObjectNotFoundException.class, () -> {
            this.serviceToTest.returnBook(book1.getId(), admin.getEmail());
        });
    }


    @Test
    void testRenewCheckout() {
        this.bookRepository.save(book1);
        checkout1.setCheckoutDate(LocalDate.now().minusDays(5));
        checkout1.setReturnDate(LocalDate.now().plusDays(2));
        this.checkoutRepository.save(checkout1);
        this.serviceToTest.renewCheckout(book1.getId(), user.getEmail());
        LocalDate returnDate = this.checkoutRepository.findById(checkout1.getId()).get()
                .getReturnDate();
        assertEquals(LocalDate.now().plusDays(7), returnDate);

    }

    @Test
    void testRenewCheckoutWhenCheckoutNotFound() {
        this.bookRepository.save(book1);
        checkout1.setCheckoutDate(LocalDate.now().minusDays(5));
        checkout1.setReturnDate(LocalDate.now().plusDays(2));
        this.checkoutRepository.save(checkout1);
        assertThrows(ObjectNotFoundException.class, () -> {
            this.serviceToTest.renewCheckout(book1.getId(), admin.getEmail());
        });
    }

    @Test
    void testBookAlreadyCheckedOutByUser() {
        this.bookRepository.save(book1);
        this.checkoutRepository.save(checkout1);
        assertTrue(this.serviceToTest.bookAlreadyCheckedOutByUser(book1.getId(), user.getEmail()));
    }

    @Test
    void testBookAlreadyCheckedOutByUserWhenNotCheckedOut() {
        this.bookRepository.save(book1);
        assertFalse(this.serviceToTest.bookAlreadyCheckedOutByUser(book1.getId(), user.getEmail()));
    }

    @Test
    void testGetLoansCount() {
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

        assertEquals(5, this.serviceToTest.getLoansCount(user.getEmail()));
    }

    @Test
    void testIsUserBlockedWithPastReturnDate() {
        this.bookRepository.save(book1);
       checkout1.setReturnDate(LocalDate.now().minusDays(1));
        this.checkoutRepository.save(checkout1);

        assertTrue(this.serviceToTest.isUserBlocked(user.getEmail()));

    }

    @Test
    void testIsUserBlockedWithFutureReturnDate() {
        this.bookRepository.save(book1);
        checkout1.setReturnDate(LocalDate.now().plusDays(1));
        this.checkoutRepository.save(checkout1);

        assertFalse(this.serviceToTest.isUserBlocked(user.getEmail()));
    }

    @Test
    void testIsUserBlockedWithPresentReturnDate() {
        this.bookRepository.save(book1);
        checkout1.setReturnDate(LocalDate.now());
        this.checkoutRepository.save(checkout1);

        assertFalse(this.serviceToTest.isUserBlocked(user.getEmail()));
    }

    @Test
    @Transactional
    void testDeleteBookCheckouts() {
           this.bookRepository.save(book1);
           this.bookRepository.save(book2);
           this.bookRepository.save(book3);

           this.checkoutRepository.save(checkout1);
           this.checkoutRepository.save(checkout2);
           this.checkoutRepository.save(checkout3);

           assertEquals(3, this.checkoutRepository.count());
           this.serviceToTest.deleteBookCheckouts(book1.getId());
           assertEquals(2, this.checkoutRepository.count());


       }

         @Test
         void testCheckIfUserHasBook() {
                this.bookRepository.save(book1);
                this.checkoutRepository.save(checkout1);

                assertThrows(NotAllowedException.class, () -> {
                    this.serviceToTest.checkIfUserHasBook(user.getEmail(), book2.getId());
                });
         }
}