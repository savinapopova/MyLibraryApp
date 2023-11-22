package com.example.mylibrary.service;



import com.example.mylibrary.event.BookReturnedEvent;
import com.example.mylibrary.event.CheckoutCreatedEvent;
import com.example.mylibrary.model.dto.AddBookDTO;
import com.example.mylibrary.model.dto.BookDTO;
import com.example.mylibrary.model.dto.SearchBookDTO;
import com.example.mylibrary.model.entity.Book;
import org.springframework.context.event.EventListener;

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
