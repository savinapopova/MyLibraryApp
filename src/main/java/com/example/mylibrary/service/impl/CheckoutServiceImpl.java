package com.example.mylibrary.service.impl;

import com.example.mylibrary.errors.NotAllowedException;
import com.example.mylibrary.errors.ObjectNotFoundException;
import com.example.mylibrary.model.dto.CheckOutDTO;
import com.example.mylibrary.model.entity.Book;
import com.example.mylibrary.model.entity.Checkout;
import com.example.mylibrary.model.entity.User;
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

    private BookService bookService;

    private CheckoutRepository checkoutRepository;

    private UserService userService;

    private HistoryService historyService;



    private ModelMapper modelMapper;

    public CheckoutServiceImpl(BookService bookService, CheckoutRepository checkoutRepository,
                               UserService userService, HistoryService historyService,
                               ModelMapper modelMapper) {
        this.bookService = bookService;
        this.checkoutRepository = checkoutRepository;
        this.userService = userService;
        this.historyService = historyService;
        this.modelMapper = modelMapper;
    }


    @Override
    public void checkoutBook(Long id, String email) {
        // TODO handled
        User user = this.userService.getUser(email);
        Book book = this.bookService.getBook(id);
        if (bookAlreadyCheckedOutByUser(id, email) || getLoansCount(email) >= 5
                || book.getCopiesAvailable() <= 0) {
            throw new NotAllowedException();
        }

        Checkout checkout = new Checkout(book, user);
        this.checkoutRepository.save(checkout);

        this.bookService.decreaseCopiesAvailable(book);




    }

    @Override
    public List<CheckOutDTO> getUserCheckouts(Long id) {
        List<Checkout> checkouts = this.checkoutRepository.findAllByUserIdOrderByCheckoutDate(id);
        return checkouts.stream()
                .map(c -> modelMapper.map(c, CheckOutDTO.class))
                .map(c -> c.setDaysLeft(TimeConverter.getTimeDifference(c)))
                .collect(Collectors.toList());

    }

    @Override
    public void returnBook(Long bookId, String email) {

        Checkout checkout = getCheckout( email, bookId);

        Book book = checkout.getBook();

        this.bookService.increaseCopiesAvailable(book);

        this.checkoutRepository.delete(checkout);


        this.historyService.registerHistory(checkout);
    }

    private Checkout getCheckout(String email, Long bookId) {
//TODO: handled
        Optional<Checkout> optionalCheckout = this.checkoutRepository
                .findByUserEmailAndBookId(email, bookId);
        if (optionalCheckout.isEmpty()) {
            throw new ObjectNotFoundException("checkout not found");
        }
        return optionalCheckout.get();
    }

    @Override
    public void renewCheckout(Long bookId, String email) {
        Checkout checkout = getCheckout(email, bookId);
        checkout.setReturnDate(LocalDate.now().plusDays(7));
        this.checkoutRepository.save(checkout);

    }

    @Override
    public boolean bookAlreadyCheckedOutByUser(Long bookId, String email) {
        Optional<Checkout> optionalCheckout = this.checkoutRepository
                .findByUserEmailAndBookId(email, bookId);
        if (optionalCheckout.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public int getLoansCount(String email) {
       return this.checkoutRepository.findAllByUserEmail(email).size();
    }

    @Override
    public boolean isUserBlocked(String email) {
        User loggedUser = this.userService.getUser(email);

        List<CheckOutDTO> userCheckouts = getUserCheckouts(loggedUser.getId());
        CheckOutDTO checkOutDTO = userCheckouts.stream()
                .filter(c -> c.getDaysLeft() < 0).findAny().orElse(null);
        if (checkOutDTO != null) {
            return true;
        }
        return false;
    }

    @Override
    public void deleteBookCheckouts(Long id) {
        this.checkoutRepository.deleteAllByBookId(id);
    }

    @Override
    public void checkIfUserHasBook(String userEmail, Long bookId) {
        Optional<Checkout> optionalCheckout = this.checkoutRepository
                .findByUserEmailAndBookId(userEmail, bookId);

        if (optionalCheckout.isEmpty()) {
            throw new NotAllowedException();
        }

    }


}



