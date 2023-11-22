package com.example.mylibrary.service.impl;

import com.example.mylibrary.errors.ObjectNotFoundException;
import com.example.mylibrary.event.BookReturnedEvent;
import com.example.mylibrary.event.CheckoutCreatedEvent;
import com.example.mylibrary.model.dto.AddBookDTO;
import com.example.mylibrary.model.dto.BookDTO;
import com.example.mylibrary.model.dto.SearchBookDTO;
import com.example.mylibrary.model.entity.Book;
import com.example.mylibrary.model.entity.Category;
import com.example.mylibrary.model.enums.CategoryName;
import com.example.mylibrary.repository.BookRepository;
import com.example.mylibrary.service.BookService;
import com.example.mylibrary.service.CategoryService;
import com.example.mylibrary.util.TextResizer;
import org.modelmapper.ModelMapper;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
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
    public List<SearchBookDTO> getSearchedBooks() {
        return this.bookRepository.findAll()
                .stream()
                .map(TextResizer::resizeDescription)
                .map(b -> this.modelMapper.map(b, SearchBookDTO.class))
                .collect(Collectors.toList());
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
    public List<SearchBookDTO> getBooksByTitle(String title) {

        if (title == null || title.trim().isBlank()) {
            return getSearchedBooks();
        }


          return this.bookRepository.findByTitleContaining(title)
                    .stream()
                  .map(TextResizer::resizeDescription)
                  .map(b -> this.modelMapper.map(b, SearchBookDTO.class))
                    .collect(Collectors.toList());


    }

    @Override
    public List<SearchBookDTO> getBooksByCategory(String categoryName) {

        if (categoryName.toLowerCase().equals("all")) {
            return getSearchedBooks();

        }

        categoryService.checkCategoryNameAvailable(categoryName);

        return this.bookRepository.findAllByCategoryName(CategoryName.valueOf(categoryName.toUpperCase()))
                .stream()
                .map(TextResizer::resizeDescription)
                .map(b -> this.modelMapper.map(b, SearchBookDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<SearchBookDTO> getBooksByTitleAndCategory(String title, String category) {


        if ((category == null || category.toLowerCase().equals("all") || category.trim().isBlank()) &&
                (title == null || title.trim().isBlank())) {
            return getSearchedBooks();
        }

        if (category == null || category.equalsIgnoreCase("all") || category.trim().isBlank()) {
            return getBooksByTitle(title);
        }

        categoryService.checkCategoryNameAvailable(category);

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
