package com.example.mylibrary.service;



import com.example.mylibrary.event.BookReturnedEvent;
import com.example.mylibrary.event.CheckoutCreatedEvent;
import com.example.mylibrary.model.dto.book.AddBookDTO;
import com.example.mylibrary.model.dto.book.BookDTO;
import com.example.mylibrary.model.dto.book.SearchBookDTO;
import com.example.mylibrary.model.entity.Book;

import java.util.List;

public interface BookService {
    List<SearchBookDTO> getSearchedBooks();

    List<BookDTO> getAllBooks();

    SearchBookDTO getSearchBookDTO(Long id);

    List<SearchBookDTO> getBooksByTitle(String title);
    List<SearchBookDTO> getBooksByCategory(String category);

    List<SearchBookDTO> getBooksByTitleAndCategory(String title, String category);

    BookDTO getBookDTO(Long id);

    Book getBook(Long id);


    void registerBook(AddBookDTO addBookDTO);


    void deleteBook(Long id);


    void decreaseCopiesAvailable(CheckoutCreatedEvent checkoutCreatedEvent);

    void increaseCopiesAvailable(BookReturnedEvent bookReturnedEvent);

    void saveBook(Book book);
}
