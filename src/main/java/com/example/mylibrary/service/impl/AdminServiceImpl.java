package com.example.mylibrary.service.impl;

import com.example.mylibrary.model.dto.AddBookDTO;
import com.example.mylibrary.model.dto.MessageDTO;
import com.example.mylibrary.model.dto.MessageResponseDTO;
import com.example.mylibrary.model.dto.UserDTO;
import com.example.mylibrary.model.entity.Book;
import com.example.mylibrary.model.entity.Role;
import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.model.enums.RoleName;
import com.example.mylibrary.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    private MessageService messageService;

    private UserService userService;

    private BookService bookService;

    private CheckoutService checkoutService;

    private RoleService roleService;

    private HistoryService historyService;

    private ReviewService reviewService;

    private ModelMapper modelMapper;

    public AdminServiceImpl(MessageService messageService, UserService userService,
                            BookService bookService, CheckoutService checkoutService,
                            RoleService roleService, HistoryService historyService,
                            ReviewService reviewService,
                            ModelMapper modelMapper) {
        this.messageService = messageService;
        this.userService = userService;
        this.bookService = bookService;
        this.checkoutService = checkoutService;
        this.roleService = roleService;
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

    @Override
    public List<UserDTO> getAllUsersExceptPrincipal(Principal principal) {

        List<User> users = this.userService.findAllUsers()
                .stream().filter(u -> !u.getEmail().equals(principal.getName()))
                .collect(Collectors.toList());

        List<UserDTO> userDTOs = new ArrayList<>();


        for (User user : users) {
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            List<String> roles = user.getRoles().stream()
                    .map(r -> r.getName().toString())
                    .collect(Collectors.toList());
            userDTO.setRoles(roles.toString().replaceAll("[\\[\\]]", ""));
            userDTO.setActive(checkActive(user));
            userDTO.setAdmin(checkAdmin(user));
            userDTOs.add(userDTO);

        }
        return userDTOs;

    }

    @Override
    public void addAdmin(Long id) {
        User user = this.userService.getUser(id);
        Role adminRole = this.roleService.findByName(RoleName.ADMIN);
        user.getRoles().add(adminRole);
        this.userService.saveUser(user);
    }

    @Override
    public void removeAdmin(Long id, Principal principal) {
        User user = this.userService.getUser(id);
        if (!user.getEmail().equals(principal.getName()) && checkAdmin(user)) {
            Role adminRole = this.roleService.findByName(RoleName.ADMIN);
            user.getRoles().remove(adminRole);
            this.userService.saveUser(user);
        }
    }

    private boolean checkAdmin(User user) {
        Role adminRole = this.roleService.findByName(RoleName.ADMIN);
       if (user.getRoles().contains(adminRole)) {
           return true;
       }
       return false;
       }



    private boolean checkActive(User user) {
        int loansCount = this.checkoutService.getLoansCount(user.getEmail());
        if (loansCount > 0) {
            return true;
        }
        return false;
    }



}
