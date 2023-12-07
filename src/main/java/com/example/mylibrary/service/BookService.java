package com.example.mylibrary.service;



import com.example.mylibrary.event.BookReturnedEvent;
import com.example.mylibrary.event.CheckoutCreatedEvent;
import com.example.mylibrary.model.dto.book.AddBookDTO;
import com.example.mylibrary.model.dto.book.BookDTO;
import com.example.mylibrary.model.dto.book.SearchBookDTO;
import com.example.mylibrary.model.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {
    Page<SearchBookDTO> getSearchedBooks(Pageable pageable);

    List<BookDTO> getAllBooks();

    SearchBookDTO getSearchBookDTO(Long id);

    Page<SearchBookDTO> getBooksByTitle(String title, Pageable pageable);
    Page<SearchBookDTO> getBooksByCategory(String category, Pageable pageable);

    Page<SearchBookDTO> getBooksByTitleAndCategory(String title, String category, Pageable pageable);

    BookDTO getBookDTO(Long id);

    Book getBook(Long id);


    void registerBook(AddBookDTO addBookDTO);


    void deleteBook(Long id);


    void decreaseCopiesAvailable(CheckoutCreatedEvent checkoutCreatedEvent);

    void increaseCopiesAvailable(BookReturnedEvent bookReturnedEvent);

    void saveBook(Book book);
}
