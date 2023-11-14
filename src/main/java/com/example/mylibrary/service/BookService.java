package com.example.mylibrary.service;



import com.example.mylibrary.model.dto.BookDTO;
import com.example.mylibrary.model.dto.SearchBookDTO;
import com.example.mylibrary.model.entity.Book;

import java.util.List;

public interface BookService {
    List<SearchBookDTO> getAllBooks();

    SearchBookDTO getSearchBookDTO(Long id);

    List<SearchBookDTO> getBooksByTitle(String title);
    List<SearchBookDTO> getBooksByCategory(String category);

    List<SearchBookDTO> getBooksByTitleAndCategory(String title, String category);

    BookDTO getBookDTO(Long id);

    Book getBook(Long id);

    void decreaseCopiesAvailable(Book book);
    void increaseCopiesAvailable(Book book);

}
