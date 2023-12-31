package com.example.mylibrary.service.impl;

import com.example.mylibrary.exceptions.ObjectNotFoundException;
import com.example.mylibrary.event.BookReturnedEvent;
import com.example.mylibrary.event.CheckoutCreatedEvent;
import com.example.mylibrary.model.dto.book.AddBookDTO;
import com.example.mylibrary.model.dto.book.BookDTO;
import com.example.mylibrary.model.dto.book.SearchBookDTO;
import com.example.mylibrary.model.entity.Book;
import com.example.mylibrary.model.entity.Category;
import com.example.mylibrary.model.entity.Checkout;
import com.example.mylibrary.model.enums.CategoryName;
import com.example.mylibrary.repository.BookRepository;
import com.example.mylibrary.service.BookService;
import com.example.mylibrary.service.CategoryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookServiceImplTestIT {

    @Autowired
    private BookRepository bookRepository;


    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BookService serviceToTest;



    private Pageable pageable;


    private Book book1;

    private Book book2;

    @BeforeEach
    void setUp() {
        this.bookRepository.deleteAll();
        Category biography = this.categoryService.getCategory(CategoryName.BIOGRAPHY);
        Category cookbook = this.categoryService.getCategory(CategoryName.COOKBOOK);
        book1 = new Book(1L, "title1", "author1",
                "image1", "description1", 1, 1, biography);
        book2 = new Book(2L, "title2", "author2",
                "image2", "description2", 2, 2, cookbook);

        pageable = PageRequest.of(0, 5);
    }

    @AfterEach
    void tearDown() {
        this.bookRepository.deleteAll();
    }

    @Test
    void testGetAllBooks() {
        this.bookRepository.save(book1);
        this.bookRepository.save(book2);

        List<BookDTO> books = this.serviceToTest.getAllBooks();

        assertEquals(2, books.size());
        assertEquals(book1.getTitle(), books.get(0).getTitle());
        assertEquals(BookDTO.class, books.get(0).getClass());
    }

    @Test
    void testGetSearchedBooks() {
        this.bookRepository.save(book1);
        this.bookRepository.save(book2);

        Page<SearchBookDTO> books = this.serviceToTest.getSearchedBooks(pageable);

        assertEquals(2, books.getTotalElements());
        assertEquals(book1.getTitle(), books.getContent().get(0).getTitle());
        assertEquals(SearchBookDTO.class, books.getContent().get(0).getClass());
    }

    @Test
    void testGetSearchBookDTO() {
        this.bookRepository.save(book1);
        this.bookRepository.save(book2);

        SearchBookDTO book = this.serviceToTest.getSearchBookDTO(1L);

        assertEquals(book1.getTitle(), book.getTitle());
        assertEquals(SearchBookDTO.class, book.getClass());
    }

    @Test
    void testGetBookDTO() {
        this.bookRepository.save(book1);
        this.bookRepository.save(book2);

        BookDTO book = this.serviceToTest.getBookDTO(1L);

        assertEquals(book1.getTitle(), book.getTitle());
        assertEquals(BookDTO.class, book.getClass());
    }

    @Test
    void testGetBookAvailable() {
        this.bookRepository.save(book1);
        this.bookRepository.save(book2);

        Book book = this.serviceToTest.getBook(1L);

        assertEquals(book1.getTitle(), book.getTitle());

    }

    @Test
    void testGetBookUnavailable() {

        this.bookRepository.save(book1);
        this.bookRepository.save(book2);

        assertThrows(ObjectNotFoundException.class, () -> this.serviceToTest.getBook(3L));

        try {
            this.serviceToTest.getBook(3L);
        } catch (ObjectNotFoundException e) {
            assertEquals("book with id 3 not found", e.getMessage());
        }
    }

    @Test
    void testDecreaseCopiesAvailable() {
        this.bookRepository.save(book1);
        this.bookRepository.save(book2);

        Book book = this.serviceToTest.getBook(1L);

        assertEquals(1, book.getCopiesAvailable());

        CheckoutCreatedEvent checkoutCreatedEvent = new CheckoutCreatedEvent(this).setBook(book);

        this.serviceToTest.decreaseCopiesAvailable(checkoutCreatedEvent);

        assertEquals(0, book.getCopiesAvailable());

    }

    @Test
    void testIncreaseCopiesAvailable() {
        this.bookRepository.save(book1);
        this.bookRepository.save(book2);


        Book book = this.serviceToTest.getBook(1L);

        assertEquals(1, book.getCopiesAvailable());

        Checkout checkout = new Checkout(book, null);

        BookReturnedEvent bookReturnedEvent = new BookReturnedEvent(this).setCheckout(checkout);

        this.serviceToTest.increaseCopiesAvailable(bookReturnedEvent);

        assertEquals(2, book.getCopiesAvailable());

    }

    @Test
    void testGetBooksByTitle() {
        this.bookRepository.save(book1);
        this.bookRepository.save(book2);

        Page<SearchBookDTO> booksByTitle = this.serviceToTest.getBooksByTitle("title1", pageable);

        assertEquals(1, booksByTitle.getTotalElements());
        assertEquals(book1.getAuthor(), booksByTitle.getContent().get(0).getAuthor());
        assertEquals(SearchBookDTO.class, booksByTitle.getContent().get(0).getClass());


    }

    @Test
    void testGetBooksByTitleBlank() {
        this.bookRepository.save(book1);
        this.bookRepository.save(book2);

        Page<SearchBookDTO> booksByTitle = this.serviceToTest.getBooksByTitle("", pageable);
        Page<SearchBookDTO> searchedBooks = this.serviceToTest.getSearchedBooks(pageable);

        assertEquals(2, booksByTitle.getTotalElements());
        assertEquals(book1.getAuthor(), booksByTitle.getContent().get(0).getAuthor());
        assertEquals(book2.getAuthor(), booksByTitle.getContent().get(1).getAuthor());
        assertEquals(booksByTitle.getTotalElements(), searchedBooks.getTotalElements());


    }

    @Test
    void testGetBooksByTitleAndCategory() {
        this.bookRepository.save(book1);
        this.bookRepository.save(book2);

        Page<SearchBookDTO> books = this.serviceToTest.getBooksByTitleAndCategory("title1", "BIOGRAPHY", pageable);



        assertEquals(1, books.getTotalElements());
        assertEquals(book1.getAuthor(), books.getContent().get(0).getAuthor());
        assertEquals(SearchBookDTO.class, books.getContent().get(0).getClass());

    }

    @Test
    void testGetBooksByTitleAndCategoryWithUnavailableTitle() {
        this.bookRepository.save(book1);
        this.bookRepository.save(book2);

        Page<SearchBookDTO> books = this.serviceToTest.getBooksByTitleAndCategory("title3", "BIOGRAPHY", pageable);


        assertEquals(0, books.getTotalElements());

    }

    @Test
    void testGetBooksByTitleAndCategoryWithUnavailableCategory() {
        this.bookRepository.save(book1);
        this.bookRepository.save(book2);

        Page<SearchBookDTO> books = this.serviceToTest.getBooksByTitleAndCategory("title3", "FANTASY", pageable);


        assertEquals(0, books.getTotalElements());

    }

    @Test
    void testGetBooksByTitleAndCategoryWithBlankCategory() {
        this.bookRepository.save(book1);
        this.bookRepository.save(book2);

        Page<SearchBookDTO> books = this.serviceToTest.getBooksByTitleAndCategory("title1", "", pageable);
        Page<SearchBookDTO> searchedBooks = this.serviceToTest.getBooksByTitle("title1", pageable);


        assertEquals(1, books.getTotalElements());
        assertEquals(book1.getAuthor(), books.getContent().get(0).getAuthor());
        assertEquals(SearchBookDTO.class, books.getContent().get(0).getClass());
        assertEquals(searchedBooks.getContent().get(0).getAuthor(), books.getContent().get(0).getAuthor());

    }

    @Test
    void testGetBooksByTitleAndCategoryWithBlankTitle() {
        this.bookRepository.save(book1);
        this.bookRepository.save(book2);

        Page<SearchBookDTO> books = this.serviceToTest.getBooksByTitleAndCategory("", "BIOGRAPHY",pageable);
        Page<SearchBookDTO> searchedBooks = this.serviceToTest.getBooksByCategory("BIOGRAPHY", pageable);


        assertEquals(1, books.getTotalElements());
        assertEquals(book1.getAuthor(), books.getContent().get(0).getAuthor());
        assertEquals(SearchBookDTO.class, books.getContent().get(0).getClass());
        assertEquals(searchedBooks.getContent().get(0).getAuthor(), books.getContent().get(0).getAuthor());

    }

    @Test
    void testGetBooksByTitleAndCategoryWithNotExistingCategory() {
        this.bookRepository.save(book1);
        this.bookRepository.save(book2);


        assertThrows(ObjectNotFoundException.class, ()
                -> this.serviceToTest.getBooksByTitleAndCategory("title3", "MISSING", pageable));

        try {
            this.serviceToTest.getBooksByTitleAndCategory("title3", "MISSING", pageable);
        } catch (ObjectNotFoundException e) {
            assertEquals("category MISSING not found", e.getMessage());

        }

    }

    @Test
    void testGetBooksByCategoryAvailable() {
        this.bookRepository.save(book1);
        this.bookRepository.save(book2);

        Page<SearchBookDTO> books = this.serviceToTest.getBooksByCategory("BIOGRAPHY", pageable);
        assertEquals(1, books.getTotalElements());
        assertEquals(book1.getAuthor(), books.getContent().get(0).getAuthor());

    }

    @Test
    void testGetBooksByCategoryAll() {
        this.bookRepository.save(book1);
        this.bookRepository.save(book2);

        Page<SearchBookDTO> books = this.serviceToTest.getBooksByCategory("ALL",pageable);
        Page<SearchBookDTO> searchedBooks = this.serviceToTest.getSearchedBooks(pageable);
        assertEquals(2, books.getTotalElements());
        assertEquals(book1.getAuthor(), books.getContent().get(0).getAuthor());
        assertEquals(book2.getAuthor(), books.getContent().get(1).getAuthor());
        assertEquals(searchedBooks.getContent().get(0).getAuthor(), books.getContent().get(0).getAuthor());
        assertEquals(searchedBooks.getContent().get(1).getAuthor(), books.getContent().get(1).getAuthor());


    }

    @Test
    void testGetBooksByCategoryBlankAndTitleBlank() {
        this.bookRepository.save(book1);
        this.bookRepository.save(book2);

        Page<SearchBookDTO> books = this.serviceToTest.getBooksByTitleAndCategory("","", pageable);
        Page<SearchBookDTO> searchedBooks = this.serviceToTest.getSearchedBooks(pageable);
        assertEquals(2, books.getTotalElements());
        assertEquals(book1.getAuthor(), books.getContent().get(0).getAuthor());
        assertEquals(book2.getAuthor(), books.getContent().get(1).getAuthor());
        assertEquals(searchedBooks.getContent().get(0).getAuthor(), books.getContent().get(0).getAuthor());
        assertEquals(searchedBooks.getContent().get(1).getAuthor(), books.getContent().get(1).getAuthor());


    }

    @Test
    void testGetBooksByCategoryUnavailable() {
        this.bookRepository.save(book1);
        this.bookRepository.save(book2);

        Page<SearchBookDTO> books = this.serviceToTest.getBooksByCategory("FANTASY",pageable);
        assertEquals(0, books.getTotalElements());

    }

    @Test
    void testGetBooksCategoryWithNotExistingCategory() {
        this.bookRepository.save(book1);
        this.bookRepository.save(book2);


        assertThrows(ObjectNotFoundException.class, ()
                -> this.serviceToTest.getBooksByCategory( "MISSING", pageable));
        try {
            this.serviceToTest.getBooksByCategory("MISSING", pageable);
        } catch (ObjectNotFoundException e) {
            assertEquals("category MISSING not found", e.getMessage());
        }

    }

    @Test
    void testRegisterBook() {
        this.bookRepository.save(book1);
        this.bookRepository.save(book2);

        AddBookDTO addBookDTO = new AddBookDTO( "title3", "author3",
                "description3","FANTASY","image3", 3 );

        Page<SearchBookDTO> searchedBooks = this.serviceToTest.getSearchedBooks(pageable);
        assertEquals(2, searchedBooks.getTotalElements());

        this.serviceToTest.registerBook(addBookDTO);

        searchedBooks = this.serviceToTest.getSearchedBooks(pageable);
        assertEquals(3, searchedBooks.getTotalElements());

        Page<SearchBookDTO> booksByTitle = this.serviceToTest.getBooksByTitle("title3", pageable);

        assertEquals(1, booksByTitle.getTotalElements());
        assertEquals(addBookDTO.getTitle(), booksByTitle.getContent().get(0).getTitle());


    }

    @Test
    void testDeleteBook() {
        this.bookRepository.save(book1);
        this.bookRepository.save(book2);

        Page<SearchBookDTO> searchedBooks = this.serviceToTest.getSearchedBooks(pageable);
        assertEquals(2, searchedBooks.getTotalElements());

        this.serviceToTest.deleteBook(1L);

        searchedBooks = this.serviceToTest.getSearchedBooks(pageable);
        assertEquals(1, searchedBooks.getTotalElements());

        assertThrows(ObjectNotFoundException.class, () -> this.serviceToTest.getBook(1L));

        Page<SearchBookDTO> booksByTitle = this.serviceToTest.getBooksByTitle("title1", pageable);

        assertEquals(0, booksByTitle.getTotalElements());
    }
}