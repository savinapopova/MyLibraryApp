package com.example.mylibrary.web;

import com.example.mylibrary.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookControllerTest {

    @Autowired
    private BookService bookService;

}