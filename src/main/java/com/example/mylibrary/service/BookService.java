package com.example.mylibrary.service;



import com.example.mylibrary.model.dto.SearchBookDTO;
import com.example.mylibrary.model.entity.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {
    List<SearchBookDTO> getAllBooks();

    SearchBookDTO getSingleBook(Long id);

    List<SearchBookDTO> getBooksByTitle(String title);
    List<SearchBookDTO> getBooksByCategory(String category);

    List<SearchBookDTO> getBooksByTitleAndCategory(String title, String category);
}
