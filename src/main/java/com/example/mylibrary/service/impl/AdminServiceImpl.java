package com.example.mylibrary.service.impl;

import com.example.mylibrary.model.dto.AddBookDTO;
import com.example.mylibrary.model.dto.MessageDTO;
import com.example.mylibrary.model.dto.MessageResponseDTO;
import com.example.mylibrary.model.entity.Book;
import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    private MessageService messageService;

    private UserService userService;

    private BookService bookService;

    private CheckoutService checkoutService;

    private HistoryService historyService;

    private ReviewService reviewService;

    private ModelMapper modelMapper;

    public AdminServiceImpl(MessageService messageService, UserService userService,
                            BookService bookService, CheckoutService checkoutService,
                            HistoryService historyService, ReviewService reviewService,
                            ModelMapper modelMapper) {
        this.messageService = messageService;
        this.userService = userService;
        this.bookService = bookService;
        this.checkoutService = checkoutService;
        this.historyService = historyService;
        this.reviewService = reviewService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<MessageDTO> getOpenMessages() {
        return this.messageService.getOpenMessages();
    }

    @Override
    public void sendResponse(Long messageId, MessageResponseDTO messageResponseDTO,
                             Principal principal) {

        User admin = userService.getLoggedUser(principal);

        this.messageService.answerMessage(messageId,messageResponseDTO.getResponse(),admin);

    }

    @Override
    public void postBook(AddBookDTO addBookDTO) {
        this.bookService.registerBook(addBookDTO);
    }

    @Override
    public void increaseBookQuantity(Long id) {
        Book book = this.bookService.getBook(id);
        book.setCopies(book.getCopies() + 1);
        this.bookService.increaseCopiesAvailable(book);
    }

    @Override
    public void decreaseBookQuantity(Long id) {
        Book book = this.bookService.getBook(id);
        if (book.getCopies() > 0 && book.getCopiesAvailable() > 0) {
            book.setCopies(book.getCopies() - 1);
            this.bookService.decreaseCopiesAvailable(book);
        }
    }

    @Override
    public void deleteBook(Long id) {
        this.checkoutService.deleteBookCheckouts(id);
        this.historyService.deleteBookHistories(id);
        this.reviewService.deleteBookReviews(id);
        this.bookService.deleteBook(id);
    }


}
