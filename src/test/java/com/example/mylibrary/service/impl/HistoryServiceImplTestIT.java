package com.example.mylibrary.service.impl;

import com.example.mylibrary.model.dto.HistoryDTO;
import com.example.mylibrary.model.entity.*;
import com.example.mylibrary.model.enums.CategoryName;
import com.example.mylibrary.model.enums.RoleName;
import com.example.mylibrary.repository.*;
import com.example.mylibrary.service.CategoryService;
import com.example.mylibrary.service.HistoryService;
import com.example.mylibrary.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class HistoryServiceImplTestIT {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private HistoryService serviceToTest   ;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private CheckoutRepository checkoutRepository;

    @Autowired
    private CategoryService categoryService;



    @Autowired
    private UserService userService;

    @Autowired
    private BookRepository bookRepository;



    private Checkout checkout;

    private History history;

    @BeforeEach
    void setUp() {
        this.historyRepository.deleteAll();
        User user = new User("firstName", "lastName", "userEmail", "password");

        this.userService.saveUser(user);
        Category category = this.categoryService.getCategory(CategoryName.BIOGRAPHY);
        Book book = new Book(1L, "title", "author",
                "image", "description", 1, 1, category);
        this.bookRepository.save(book);
        checkout = new Checkout(book, user);
        this.checkoutRepository.save(checkout);

    }

    @AfterEach
    void tearDown() {
        historyRepository.deleteAll();
    }

    @Test
    void testGetUserHistories() {
        history = new History(checkout);
        this.historyRepository.save(history);

        List<HistoryDTO> histories = this.serviceToTest.getUserHistories("userEmail");

        HistoryDTO historyDTO = modelMapper.map(history, HistoryDTO.class);


        assertEquals(1, histories.size());
        assertEquals(historyDTO.getBook().getTitle(), histories.get(0).getBook().getTitle());
        assertEquals(historyDTO.getBook().getAuthor(), history.getBook().getAuthor());


    }

    @Test
    void testRegisterHistory() {
        this.serviceToTest.registerHistory(checkout);
        List<HistoryDTO> histories = this.serviceToTest.getUserHistories("userEmail");

        assertEquals(1, histories.size());

    }

    @Test
    @Transactional
    void testDeleteBookHistories() {
        this.serviceToTest.registerHistory(checkout);
        this.serviceToTest.deleteBookHistories(1L);
        List<HistoryDTO> histories = this.serviceToTest.getUserHistories("userEmail");

        assertEquals(0, histories.size());
    }

}