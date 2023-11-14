package com.example.mylibrary.service.impl;

import com.example.mylibrary.model.dto.AddBookDTO;
import com.example.mylibrary.model.dto.MessageDTO;
import com.example.mylibrary.model.dto.MessageResponseDTO;
import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.service.AdminService;
import com.example.mylibrary.service.BookService;
import com.example.mylibrary.service.MessageService;
import com.example.mylibrary.service.UserService;
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

    private ModelMapper modelMapper;

    public AdminServiceImpl(MessageService messageService, UserService userService, BookService bookService, ModelMapper modelMapper) {
        this.messageService = messageService;
        this.userService = userService;
        this.bookService = bookService;
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


}
