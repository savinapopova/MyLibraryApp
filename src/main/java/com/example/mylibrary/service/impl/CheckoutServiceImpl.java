package com.example.mylibrary.service.impl;

import com.example.mylibrary.model.dto.CheckOutDTO;
import com.example.mylibrary.model.entity.Book;
import com.example.mylibrary.model.entity.Checkout;
import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.repository.BookRepository;
import com.example.mylibrary.repository.CheckoutRepository;
import com.example.mylibrary.service.BookService;
import com.example.mylibrary.service.CheckoutService;
import com.example.mylibrary.service.HistoryService;
import com.example.mylibrary.service.UserService;
import com.example.mylibrary.util.TimeConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    private BookRepository bookRepository;

    private CheckoutRepository checkoutRepository;

    private UserService userService;

    private HistoryService historyService;



    private ModelMapper modelMapper;

    public CheckoutServiceImpl(BookRepository bookRepository, CheckoutRepository checkoutRepository,
                               UserService userService, HistoryService historyService,
                                ModelMapper modelMapper) {
        this.bookRepository = bookRepository;
        this.checkoutRepository = checkoutRepository;
        this.userService = userService;
        this.historyService = historyService;
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
        this.userService.saveUser(user);

        book.setCopiesAvailable(book.getCopiesAvailable() - 1);
        this.bookRepository.save(book);

    }

    @Override
    public List<CheckOutDTO> getUserCheckouts(Principal principal) {
        User user = this.userService.getLoggedUser(principal);
        List<Checkout> checkouts = this.checkoutRepository.findAllByUserIdOrderByCheckoutDate(user.getId());
        return checkouts.stream()
                .map(c -> modelMapper.map(c, CheckOutDTO.class))
                .map(c -> c.setDaysLeft(TimeConverter.getTimeDifference(c)))
                .collect(Collectors.toList());

    }

    @Override
    public void returnBook(Long id, Principal principal) {

        User user = this.userService.getLoggedUser(principal);


        Optional<Checkout> optionalCheckout = this.checkoutRepository
                .findByUserEmailAndBookId(user.getEmail(), id);
        if (optionalCheckout.isEmpty()) {
            throw new NoSuchElementException();
        }
        Checkout checkout = getCheckout(user.getEmail(), id);

        user.getBooks().remove(checkout.getBook());
        this.userService.saveUser(user);
        this.checkoutRepository.delete(checkout);


        this.historyService.registerHistory(checkout);
    }

    private Checkout getCheckout(String email, Long id) {
//TODO: handle exception
        Optional<Checkout> optionalCheckout = this.checkoutRepository
                .findByUserEmailAndBookId(email, id);
        if (optionalCheckout.isEmpty()) {
            throw new NoSuchElementException();
        }
        return optionalCheckout.get();
    }

    @Override
    public void renewCheckout(Long id, Principal principal) {
        User user = this.userService.getLoggedUser(principal);
        Checkout checkout = getCheckout(user.getEmail(), id);
        checkout.setReturnDate(LocalDate.now().plusDays(7));
        this.checkoutRepository.save(checkout);

    }
}



