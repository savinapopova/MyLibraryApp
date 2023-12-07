package com.example.mylibrary.service.impl;

import com.example.mylibrary.exceptions.ObjectNotFoundException;
import com.example.mylibrary.event.BookReturnedEvent;
import com.example.mylibrary.event.CheckoutCreatedEvent;
import com.example.mylibrary.model.dto.book.AddBookDTO;
import com.example.mylibrary.model.dto.book.BookDTO;
import com.example.mylibrary.model.dto.book.SearchBookDTO;
import com.example.mylibrary.model.entity.Book;
import com.example.mylibrary.model.entity.Category;
import com.example.mylibrary.model.enums.CategoryName;
import com.example.mylibrary.repository.BookRepository;
import com.example.mylibrary.service.BookService;
import com.example.mylibrary.service.CategoryService;
import com.example.mylibrary.util.TextResizer;
import org.modelmapper.ModelMapper;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository bookRepository;
    private ModelMapper modelMapper;

    private CategoryService categoryService;

    public BookServiceImpl(BookRepository bookRepository, ModelMapper modelMapper, CategoryService categoryService) {
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;
        this.categoryService = categoryService;
    }

    @Override
    public Page<SearchBookDTO> getSearchedBooks(Pageable pageable) {
        return this.bookRepository.findAll(pageable)

                .map(TextResizer::resizeDescription)
                .map(b -> this.modelMapper.map(b, SearchBookDTO.class));
    }

    @Override
    public List<BookDTO> getAllBooks() {
        return this.bookRepository.findAll()
                .stream()
                .map(TextResizer::resizeDescription)
                .map(b -> this.modelMapper.map(b, BookDTO.class))
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
        // TODO: handled
        Optional<Book> optionalBook = this.bookRepository.findById(id);
        if (optionalBook.isEmpty()) {
            throw new ObjectNotFoundException("book with id " + id + " not found");
        }
        return optionalBook.get();
    }

    @Override
    @EventListener(BookReturnedEvent.class)
    public void increaseCopiesAvailable(BookReturnedEvent bookReturnedEvent) {
        Book book = bookReturnedEvent.getCheckout().getBook();
        book.setCopiesAvailable(book.getCopiesAvailable() + 1);
        this.bookRepository.save(book);
    }

    @Override
    @EventListener(CheckoutCreatedEvent.class)
    public void decreaseCopiesAvailable(CheckoutCreatedEvent checkoutCreatedEvent) {
        Book book = checkoutCreatedEvent.getBook();
        book.setCopiesAvailable(book.getCopiesAvailable() - 1);
        this.bookRepository.save(book);
    }

    @Override
    public void saveBook(Book book) {
        this.bookRepository.save(book);
    }



    @Override
    public void registerBook(AddBookDTO addBookDTO) {
        Book book = modelMapper.map(addBookDTO, Book.class);
        book.setCopiesAvailable(addBookDTO.getCopies());
        Category category = this.categoryService.getCategory(CategoryName.valueOf(addBookDTO.getCategory().toUpperCase()));
        book.setCategory(category);
        this.bookRepository.save(book);
    }

    @Override
    public void deleteBook(Long id) {
        this.bookRepository.deleteById(id);
    }


    @Override
    public Page<SearchBookDTO> getBooksByTitle(String title, Pageable pageable) {

        if (title == null || title.trim().isBlank()) {
            return getSearchedBooks(pageable);
        }


        return this.bookRepository.findByTitleContaining(title, pageable)

                .map(TextResizer::resizeDescription)
                .map(b -> this.modelMapper.map(b, SearchBookDTO.class));


    }

    @Override
    public Page<SearchBookDTO> getBooksByCategory(String categoryName, Pageable pageable) {

        if (categoryName.toLowerCase().equals("all")) {
            return getSearchedBooks(pageable);

        }

        categoryService.checkCategoryNameAvailable(categoryName);

        return this.bookRepository.findAllByCategoryName(CategoryName.valueOf(categoryName.toUpperCase()), pageable)

                .map(TextResizer::resizeDescription)
                .map(b -> this.modelMapper.map(b, SearchBookDTO.class));
    }

    @Override
    public Page<SearchBookDTO> getBooksByTitleAndCategory(String title, String category, Pageable pageable) {


        if ((category == null || category.toLowerCase().equals("all") || category.trim().isBlank()) &&
                (title == null || title.trim().isBlank())) {
            return getSearchedBooks(pageable);
        }

        if (category == null || category.equalsIgnoreCase("all") || category.trim().isBlank()) {
            return getBooksByTitle(title, pageable);
        }

        categoryService.checkCategoryNameAvailable(category);

        if (title == null || title.trim().isBlank()) {
            return getBooksByCategory(category, pageable);
        }

        return this.bookRepository.findAllByCategoryNameAndTitleContaining(CategoryName.valueOf(category.toUpperCase()), title, pageable)

                .map(TextResizer::resizeDescription)
                .map(b -> this.modelMapper.map(b, SearchBookDTO.class));
    }
}
