package com.example.mylibrary.service.impl;

import com.example.mylibrary.model.dto.BookDTO;
import com.example.mylibrary.model.entity.Book;
import com.example.mylibrary.model.entity.Checkout;
import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.repository.BookRepository;
import com.example.mylibrary.repository.CheckoutRepository;
import com.example.mylibrary.repository.UserRepository;
import com.example.mylibrary.service.CheckoutService;
import com.example.mylibrary.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    private BookRepository bookRepository;

    private CheckoutRepository checkoutRepository;

    private UserRepository userRepository;

    private UserService userService;

    private ModelMapper modelMapper;

    public CheckoutServiceImpl(BookRepository bookRepository, CheckoutRepository checkoutRepository,
                               UserRepository userRepository, UserService userService, ModelMapper modelMapper) {
        this.bookRepository = bookRepository;
        this.checkoutRepository = checkoutRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }


    @Override
    public void checkoutBook(Long id, Principal principal) {
        // TODO: handle exceptions
        User user = this.userService.getLoggedUser(principal);
        Optional<Book> optionalBook = this.bookRepository.findById(id);
        if (optionalBook.isEmpty()) {
            throw new NoSuchElementException();
        }
        Book book = optionalBook.get();
        if (user.getBooks().contains(book) || user.getBooks().size() >= 5) {
            throw new UnsupportedOperationException();
        }

        Checkout checkout = new Checkout(book, user);
        this.checkoutRepository.save(checkout);

        user.getBooks().add(book);
        this.userRepository.save(user);

        book.setCopiesAvailable(book.getCopiesAvailable() - 1);
        this.bookRepository.save(book);

    }
}
