package com.example.mylibrary.service.impl;

import com.example.mylibrary.model.dto.PostMessageDTO;
import com.example.mylibrary.model.entity.Message;
import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.repository.MessageRepository;
import com.example.mylibrary.service.MessageService;
import com.example.mylibrary.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class MessageServiceImpl implements MessageService {

    private MessageRepository messageRepository;

    private UserService userService;

    private ModelMapper modelMapper;

    public MessageServiceImpl(MessageRepository messageRepository, UserService userService, ModelMapper modelMapper) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Override
    public void registerMessage(PostMessageDTO postMessageDTO, Principal principal) {

        Message message = modelMapper.map(postMessageDTO, Message.class);
        User user = this.userService.getLoggedUser(principal);
        message.setUser(user);
        this.messageRepository.save(message);

    }
}
