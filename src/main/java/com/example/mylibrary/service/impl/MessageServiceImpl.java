package com.example.mylibrary.service.impl;

import com.example.mylibrary.model.dto.MessageDTO;
import com.example.mylibrary.model.dto.PostMessageDTO;
import com.example.mylibrary.model.entity.Message;
import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.repository.MessageRepository;
import com.example.mylibrary.service.MessageService;
import com.example.mylibrary.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<MessageDTO> getUsersMessages(Principal principal) {
      List<Message> messages = this.messageRepository.findAllByUserEmail(principal.getName());

      return messages.stream().map(message -> this.modelMapper.map(message, MessageDTO.class))
              .collect(Collectors.toList());
    }
}
