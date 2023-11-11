package com.example.mylibrary.service.impl;

import com.example.mylibrary.model.dto.BookDTO;
import com.example.mylibrary.model.dto.SearchBookDTO;
import com.example.mylibrary.model.entity.Book;
import com.example.mylibrary.model.enums.CategoryName;
import com.example.mylibrary.repository.BookRepository;
import com.example.mylibrary.service.BookService;
import com.example.mylibrary.util.TextResizer;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository bookRepository;
    private ModelMapper modelMapper;

    public BookServiceImpl(BookRepository bookRepository, ModelMapper modelMapper) {
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<SearchBookDTO> getAllBooks() {
        return this.bookRepository.findAll()
                .stream()
                .map(TextResizer::resizeDescription)
                .map(b -> this.modelMapper.map(b, SearchBookDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public SearchBookDTO getSearchBookDTO(Long id) {

            Book book = getBook(id);

            return modelMapper.map(book, SearchBookDTO.class);

    }

    @Override
    public BookDTO getBookDTO(Long id) {
        Book book = getBook(id);
        return modelMapper.map(book, BookDTO.class);
    }



    public Book getBook(Long id) {
        // TODO: handle exception
        Optional<Book> optionalBook = this.bookRepository.findById(id);
        if (optionalBook.isEmpty()) {
            throw new NoSuchElementException();
        }
        return optionalBook.get();
    }


    @Override
    public List<SearchBookDTO> getBooksByTitle(String title) {

        if (title == null || title.trim().isBlank()) {
            return getAllBooks();
        }


          return this.bookRepository.findByTitleContaining(title)
                    .stream()
                  .map(TextResizer::resizeDescription)
                  .map(b -> this.modelMapper.map(b, SearchBookDTO.class))
                    .collect(Collectors.toList());


    }

    @Override
    public List<SearchBookDTO> getBooksByCategory(String category) {

        if (category.toLowerCase().equals("all")) {
            return getAllBooks();

        }

        return this.bookRepository.findAllByCategoryName(CategoryName.valueOf(category.toUpperCase()))
                .stream()
                .map(TextResizer::resizeDescription)
                .map(b -> this.modelMapper.map(b, SearchBookDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<SearchBookDTO> getBooksByTitleAndCategory(String title, String category) {
        if ((category == null || category.toLowerCase().equals("all")) &&
                (title == null || title.trim().isBlank())) {
            return getAllBooks();
        }

        if (category == null || category.toLowerCase().equals("all")) {
            return getBooksByTitle(title);
        }

        if (title == null || title.trim().isBlank()) {
            return getBooksByCategory(category);
        }

        return this.bookRepository.findAllByCategoryNameAndTitleContaining(CategoryName.valueOf(category.toUpperCase()), title)
                .stream()
                .map(TextResizer::resizeDescription)
                .map(b -> this.modelMapper.map(b, SearchBookDTO.class))
                .collect(Collectors.toList());
    }
}
